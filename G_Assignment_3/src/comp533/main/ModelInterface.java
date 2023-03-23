package comp533.main;

import comp533.barrier.BarrierInterface;
import comp533.client.ClientInterface;
import comp533.joiner.JoinerInterface;
import comp533.keyValue.KeyValueInterface;
import comp533.view.ViewInterface;
import util.models.PropertyListenerRegisterer;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;

public interface ModelInterface<K, V> extends PropertyListenerRegisterer {
    String COUNTER_NAME = "Counter";
    Map<String, Integer> getResult();
    ArrayBlockingQueue<KeyValueInterface<K, V>> getBoundedBuffer();
    ArrayList<ConcurrentLinkedQueue<KeyValueInterface<K, V>>> getReductionQueueList();
    BarrierInterface getBarrier();
    JoinerInterface getJoiner();
    void interruptThreads();
    void setInputString(String newInputString, ViewInterface view);
    void updateThreads();
    void setNumThreads(int numThreads, ViewInterface view);
}
