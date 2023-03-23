package comp533.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;

import comp533.barrier.TokenCounterBarrier;
import comp533.joiner.Joiner;
import comp533.keyValue.KeyValue;
import comp533.view.TokenCounterView;

public interface ModelInterface {
	public void setInputString(String newInputString, TokenCounterView view);
	
	public String getInputString();
	    
    public Map<String, Integer> getResult();

	public void setNumThreads(int numThreads, TokenCounterView view);

	public void interruptThreads();

	public BlockingQueue<KeyValue<String, Integer>> getKeyValueQueue();

	public List<ConcurrentLinkedQueue<KeyValue<String, Integer>>> getReductionQueueList();

	public TokenCounterBarrier getBarrier();

	public Joiner getJoiner();

	public BlockingQueue<KeyValue<String, Integer>> getKeyValueQueueForTest();

	public List<ConcurrentLinkedQueue<KeyValue<String, Integer>>> getReductionQueueListForTest();
}
