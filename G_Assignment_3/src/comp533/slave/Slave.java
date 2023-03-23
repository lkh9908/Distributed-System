package comp533.slave;

import comp533.barrier.BarrierInterface;
import comp533.client.ClientInterface;
import comp533.joiner.JoinerInterface;
import comp533.keyValue.KeyValueInterface;
import comp533.main.ModelInterface;
import comp533.keyValue.KeyValue;
import comp533.partitioner.Partitioner;
import comp533.partitioner.PartitionerFactory;
import comp533.reducer.ReducerInterface;
import comp533.reducer.ReducerFactory;
import gradingTools.comp533s19.assignment0.AMapReduceTracer;
import util.trace.Tracer;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Slave extends AMapReduceTracer implements SlaveInterface {
    private int threadId;
    private ClientInterface client;
    //fan si le
    private List<KeyValueInterface<String, Integer>> keyValueList;
    private ModelInterface counter;

    public Slave(int threadId, ModelInterface counter) {
        this.threadId = threadId;
        this.counter = counter;
        this.keyValueList = new ArrayList<>();
    }

    public synchronized void notifySlave() {
        this.synchronizedNotify();
    }

    public void splitBoundedBuffer() throws InterruptedException {
        ArrayBlockingQueue<KeyValueInterface<String, Integer>> boundedBuffer = this.counter.getBoundedBuffer();
        KeyValueInterface<String, Integer> consumedItem = null;
        while(consumedItem == null || consumedItem.getKey() != null) {
            this.traceDequeueRequest(boundedBuffer);
            consumedItem = boundedBuffer.take();
            this.traceDequeue(consumedItem);
            if (consumedItem.getKey() != null) {
                this.keyValueList.add(consumedItem);
            }
        }
    }

    public Map<String, Integer> reduceList(ReducerInterface<String, Integer> reducer, List<KeyValueInterface<String, Integer>> keyValuePairs) {
        try {
            this.traceRemoteList(keyValuePairs);
            return this.client.reduce(keyValuePairs);
        } catch (RemoteException | NullPointerException ex) {
            return reducer.reduce(keyValuePairs);
        }

    }

    private ArrayList<ConcurrentLinkedQueue<KeyValueInterface<String, Integer>>> splitReduction(Map<String, Integer> partiallyReducedMap) {
        ArrayList<ConcurrentLinkedQueue<KeyValueInterface<String, Integer>>> reduceQueueList = this.counter.getReductionQueueList();
        Partitioner partitioner = PartitionerFactory.getPartitioner();
        for (Map.Entry<String, Integer> entry: partiallyReducedMap.entrySet()) {
            String key = entry.getKey();
            Integer value = entry.getValue();
            if (key == null) {
                break;
            }
            int numOfPartitions = reduceQueueList.size();
            int index = partitioner.getPartition(key, value, numOfPartitions);
            this.tracePartitionAssigned(key, value, index, numOfPartitions);
            KeyValueInterface<String, Integer> keyValue = new KeyValue<>(key, value);
            reduceQueueList.get(index).add(keyValue);
        }
        return reduceQueueList;
    }

    public void setClient(ClientInterface client) {
    	System.out.println("*****************************");
    	System.out.println(client.getClass());
    	System.out.println(client.toString());
        this.client = client;
        this.traceClientAssignment(client);
    }

    public void signalQuit() {
        this.traceQuit();
    }

    @Override
    public void run() {
        BarrierInterface tokenCounterBarrier = this.counter.getBarrier();
        ReducerInterface<String, Integer> reducer = ReducerFactory.getReducer();
        while(true) {
            try {
                Map<String, Integer> originalMap = this.counter.getResult();
                this.splitBoundedBuffer();
                Map<String, Integer> partiallyReducedMap = this.reduceList(reducer, this.keyValueList);
                ArrayList<ConcurrentLinkedQueue<KeyValueInterface<String, Integer>>> reductionQueueList = this.splitReduction(partiallyReducedMap);
                tokenCounterBarrier.barrier();
                this.traceSplitAfterBarrier(this.threadId, reductionQueueList);
                for (ConcurrentLinkedQueue<KeyValueInterface<String, Integer>> reductionQueue : reductionQueueList) {
                    List<KeyValueInterface<String, Integer>> keyValues = List.copyOf(reductionQueue);
                    this.reduceList(reducer, keyValues);
                }
                JoinerInterface joiner = this.counter.getJoiner();
                joiner.finished();
                this.traceAddedToMap(originalMap, this.counter.getResult());
                this.traceRemoteResult(this.counter.getResult());
                this.synchronizedWait();
                this.keyValueList = new ArrayList<>();
            } catch (InterruptedException ex) {
                Tracer.error(Arrays.toString(ex.getStackTrace()));
                break;
            }
        }
    }
}
