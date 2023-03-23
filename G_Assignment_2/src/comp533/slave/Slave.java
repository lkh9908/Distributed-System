package comp533.slave;

import comp533.TokenCounter;
import comp533.barrier.Barrier;
import comp533.barrier.TokenCounterBarrier;
import comp533.joiner.Joiner;
import comp533.keyValue.KeyValue;
import comp533.model.ModelInterface;
import comp533.model.TokenCounterModel;
import comp533.partitioner.TokenCounterPartitioner;
import comp533.partitioner.TokenCounterPartitionerFactory;
import comp533.reducer.Reducer;

import comp533.reducer.ReducerFactory;
import comp533.reducer.ReducerInterface;
import gradingTools.comp533s19.assignment0.AMapReduceTracer;
import util.trace.Tracer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Slave extends AMapReduceTracer implements SlaveInterface {
    private int threadId;
    private List<KeyValue<String, Integer>> keyValueList;
    private ModelInterface counter;

    public Slave(int threadId, ModelInterface thisModel) {
        this.threadId = threadId;
        this.counter = thisModel;
        this.keyValueList = new ArrayList<>();
    }

    public synchronized void notifySlave() {
        this.synchronizedNotify();
    }

    public List<KeyValue<String, Integer>> splitBoundedBuffer() throws InterruptedException {
        BlockingQueue<KeyValue<String, Integer>> boundedBuffer = this.counter.getKeyValueQueue();
        KeyValue<String, Integer> consumedItem = null;
        while(consumedItem == null || consumedItem.getKey() != null) {
            this.traceDequeueRequest(boundedBuffer);
            consumedItem = boundedBuffer.take();
//            System.out.println("consumedItem");
//            System.out.println(consumedItem);
//            System.out.println("consumedItem");
            this.traceDequeue(consumedItem);
            if (consumedItem.getKey() != null) {
                this.keyValueList.add(consumedItem);
//              System.out.println("keyValueList");
//              System.out.println(keyValueList);
//              System.out.println("keyValueList");
            }
        }
        return this.keyValueList;
    }

    public Map<String, Integer> reduceList(final ReducerInterface<String, Integer> reducer) {
    	System.out.println("lalalalallalalalalallaallaalalalaalalallalaalalalalalalalalalalalalal");
        System.out.println("!" + this.keyValueList);
        return reducer.reduce(this.keyValueList);
    }

    private List<ConcurrentLinkedQueue<KeyValue<String, Integer>>> splitReduction(Map<String, Integer> partiallyReducedMap) {
        List<ConcurrentLinkedQueue<KeyValue<String, Integer>>> reduceQueueList = this.counter.getReductionQueueList();
        TokenCounterPartitioner partitioner = TokenCounterPartitionerFactory.getPartitioner();
        for (Map.Entry<String, Integer> entry: partiallyReducedMap.entrySet()) {
            String key = entry.getKey();
            Integer value = entry.getValue();
            if (key == null) {
                break;
            }
            int numOfPartitions = reduceQueueList.size();
            int index = partitioner.getPartition(key, value, numOfPartitions);
            this.tracePartitionAssigned(key, value, index, numOfPartitions);
            KeyValue<String, Integer> keyValue = new KeyValue<>(key, value);
            reduceQueueList.get(index).add(keyValue);
        }
        return reduceQueueList;
    }


    public void signalQuit() {
        this.traceQuit();
    }

    @Override
    public void run() {
        Barrier tokenCounterBarrier = this.counter.getBarrier();
        ReducerInterface<String, Integer> reducer = ReducerFactory.getReducer();
        
        while(true) {
            try {
                final Map<String, Integer> originalMap = this.counter.getResult();
                this.keyValueList = this.splitBoundedBuffer();
                
                
                final Map<String, Integer> partiallyReducedMap = this.reduceList(reducer);
                final List<ConcurrentLinkedQueue<KeyValue<String, Integer>>> reductionQueueList = this.splitReduction(partiallyReducedMap);
                tokenCounterBarrier.barrier();
                this.traceSplitAfterBarrier(this.threadId, reductionQueueList);
                for (@SuppressWarnings("unused") ConcurrentLinkedQueue<KeyValue<String, Integer>> reductionQueue : reductionQueueList) {
//                    List<KeyValue<String, Integer>> keyValues = List.copyOf(reductionQueue);
                    this.reduceList(reducer);
                }
                final Joiner joiner = this.counter.getJoiner();
//                joiner.join();
                joiner.finished();
                this.traceAddedToMap(originalMap, this.counter.getResult());
                this.synchronizedWait();
                this.keyValueList = new ArrayList<>();
            } catch (InterruptedException ex) {
                Tracer.error(Arrays.toString(ex.getStackTrace()));
                break;
            }
        }
    }
    

	@Override
    public String toString() {
        return AMapReduceTracer.SLAVE;
    }
}
