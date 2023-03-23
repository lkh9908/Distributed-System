package facebook;

import comp533.barrier.BarrierInterface;
import comp533.client.ClientInterface;
import comp533.joiner.JoinerInterface;
import comp533.keyValue.KeyValueInterface;
import comp533.main.ModelInterface;
import comp533.keyValue.KeyValue;
import comp533.partitioner.Partitioner;
import comp533.partitioner.PartitionerFactory;
import comp533.reducer.ReducerInterface;
import comp533.slave.SlaveInterface;
import comp533.reducer.ReducerFactory;
import gradingTools.comp533s19.assignment0.AMapReduceTracer;
import util.trace.Tracer;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class FacebookSlave extends AMapReduceTracer implements SlaveInterface {
    private int threadId;
    private RemoteClientFacebookMapReduce client;
    private List<KeyValueInterface<String, List<String>>> keyValueList;
    private ModelInterface counter;

    public FacebookSlave(int threadId, ModelInterface counter) {
        this.threadId = threadId;
        this.counter = counter;
        this.keyValueList = new ArrayList<>();
    }
    // why the fuck is this not working??????
    public synchronized void notifySlave() {
        this.synchronizedNotify();
    }

    public void splitBoundedBuffer() throws InterruptedException {
        ArrayBlockingQueue<KeyValueInterface<String, List<String>>> boundedBuffer = this.counter.getBoundedBuffer();
        KeyValueInterface<String, List<String>> consumedItem = null;
        while(consumedItem == null || consumedItem.getKey() != null) {
            this.traceDequeueRequest(boundedBuffer);
            consumedItem = boundedBuffer.take();
            this.traceDequeue(consumedItem);
            if (consumedItem.getKey() != null) {
                this.keyValueList.add(consumedItem);
            }
        }
    }

    public Map<String, List<String>> reduceList(FacebookReducer reducer, List<KeyValueInterface<String, List<String>>> keyValuePairs) {
        try {
        	System.out.println("slave3333333333333333333333333333");
            this.traceRemoteList(keyValuePairs);
            System.out.println("slave44444444444444444444444444444");
            return this.client.reduceNew(keyValuePairs);
        } catch (RemoteException | NullPointerException ex) {
            return reducer.reduce(keyValuePairs);
        }

    }

    private ArrayList<ConcurrentLinkedQueue<KeyValueInterface<String, List<String>>>> splitReduction(Map<String, List<String>> partiallyReducedMap) {
        ArrayList<ConcurrentLinkedQueue<KeyValueInterface<String, List<String>>>> reduceQueueList = this.counter.getReductionQueueList();
        FacebookPartitioner partitioner = new FacebookPartitioner();
        for (Entry<String, List<String>> entry: partiallyReducedMap.entrySet()) {
            String key = entry.getKey();
            List<String> value = entry.getValue();
            if (key == null) {
                break;
            }
            int numOfPartitions = reduceQueueList.size();
            int index = partitioner.getPartition(key, value, numOfPartitions);
            this.tracePartitionAssigned(key, value, index, numOfPartitions);
            KeyValueInterface<String, List<String>> keyValue = new KeyValue<>(key, value);
            reduceQueueList.get(index).add(keyValue);
        }
        return reduceQueueList;
    }


    public void setClient(RemoteClientFacebookMapReduce client) {
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
	public Map<String, Integer> reduceList(ReducerInterface<String, Integer> reducer,
			List<KeyValueInterface<String, Integer>> keyValuePairs) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setClient(ClientInterface client) {
		// TODO Auto-generated method stub
		
	}
	
    @Override
    public void run() {
        BarrierInterface tokenCounterBarrier = this.counter.getBarrier();
        FacebookReducer reducer = new FacebookReducer();
        while(true) {
            try {
                Map<String, Integer> originalMap = this.counter.getResult();
                this.splitBoundedBuffer();
                Map<String, List<String>> partiallyReducedMap = this.reduceList(reducer, this.keyValueList);
                ArrayList<ConcurrentLinkedQueue<KeyValueInterface<String, List<String>>>> reductionQueueList = this.splitReduction(partiallyReducedMap);
                tokenCounterBarrier.barrier();
                this.traceSplitAfterBarrier(this.threadId, reductionQueueList);
                for (ConcurrentLinkedQueue<KeyValueInterface<String, List<String>>> reductionQueue : reductionQueueList) {
                    List<KeyValueInterface<String, List<String>>> keyValues = List.copyOf(reductionQueue);
                    this.reduceList(reducer, keyValues);
                }
                JoinerInterface joiner = this.counter.getJoiner();
                joiner.finished();
                this.traceAddedToMap(originalMap, this.counter.getResult());
                //this.traceRemoteResult(this.counter.getResult());
                this.synchronizedWait();
                this.keyValueList = new ArrayList<>();
            } catch (InterruptedException ex) {
                Tracer.error(Arrays.toString(ex.getStackTrace()));
                break;
            }
        }
    }


}
