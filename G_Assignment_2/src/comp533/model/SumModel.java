package comp533.model;

import comp533.barrier.TokenCounterBarrier;
import comp533.joiner.Joiner;
import comp533.joiner.TokenCounterJoiner;
import comp533.keyValue.KeyValue;
import comp533.mapper.Mapper;
import comp533.mapper.MapperInterface;
import comp533.mapper.SumMapper;
import comp533.slave.Slave;
import comp533.view.TokenCounterView;
import gradingTools.comp533s19.assignment0.AMapReduceTracer;
import util.trace.Tracer;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class SumModel extends AMapReduceTracer implements ModelInterface {
    private final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
    private int numThreads;
    private List<Thread> threads;
    private List<Slave> slaves;
    private String inputString;
    private Map<String, Integer> result;
    private String inputStringAsVariable = "INPUTSTRING";
    private ArrayBlockingQueue<KeyValue<String, Integer>> boundedBuffer;
    private ArrayList<ConcurrentLinkedQueue<KeyValue<String, Integer>>> reductionQueueList;
    private Joiner joiner;
    private TokenCounterBarrier barrier;

    public SumModel() {
        this.inputString = null;
        this.numThreads = 0;
        this.reductionQueueList = new ArrayList<>();
        this.slaves = new ArrayList<>();
        this.boundedBuffer = new ArrayBlockingQueue<>(this.BUFFER_SIZE);
        
    }
    
    public String getInputString() {
        return this.inputString;
    }

    public void clearReductionQueues() {
        for (ConcurrentLinkedQueue<KeyValue<String, Integer>> reductionQueue: reductionQueueList) {
            reductionQueue.clear();
        }
    }

    public void unblockSlaveThreads() {
        for (int i = 0; i < this.numThreads; i++) {
            final Slave currentSlave = this.slaves.get(i);
            currentSlave.notifySlave();
        }
    }

    public void updateInputString(final String newInputString, final TokenCounterView view) {
        final String oldInputString = this.inputString;
        this.inputString = newInputString;
        final PropertyChangeEvent updateInputStringEvent = new PropertyChangeEvent(this, inputStringAsVariable,
                oldInputString, newInputString);
        view.printNotification(updateInputStringEvent);
        this.propertyChangeSupport.firePropertyChange(updateInputStringEvent);
    }

    public void initializeStructures() {
        this.result = new HashMap<>();
        this.boundedBuffer = new ArrayBlockingQueue<>(this.BUFFER_SIZE);
        for (ConcurrentLinkedQueue<KeyValue<String, Integer>> reductionQueue: this.reductionQueueList) {
            reductionQueue.clear();
        }
//        System.out.println("reductionQueueList");
//        System.out.println((this.reductionQueueList).toString());
    }

    public void produceBoundedBuffer(final KeyValue<String, Integer> keyValue) {
        try {
            this.traceEnqueueRequest(keyValue);
            this.boundedBuffer.put(keyValue);
            this.traceEnqueue(this.boundedBuffer);
        } catch (InterruptedException ex) {
            Tracer.error(ex.getMessage());
        }
    }

    public void endEnqueue() {
        try {
            for (int i = 0; i < this.numThreads; i++) {
                final KeyValue<String, Integer> endKeyValue = new KeyValue<>(null, null);
                this.traceEnqueueRequest(endKeyValue);
                this.boundedBuffer.put(endKeyValue);
            }
        } catch (InterruptedException ex) {
            Tracer.error(ex.getMessage());
        }
    }

    public void startThreads() {
        for (Thread currentThread: this.threads) {
            if (currentThread.getState() == Thread.State.NEW) {
                currentThread.start();
            }
        }
    }

    private void problemSplit() throws InterruptedException {
        final String[] tokens = this.inputString.split(" ");
        final MapperInterface<String, Integer> mapper = new SumMapper();
        for (String token: tokens) {
            final KeyValue<String, Integer> keyValue = mapper.map(token);
            this.produceBoundedBuffer(keyValue);
        }
        this.endEnqueue();
    }

    private void mergeIntermediaryResults(final TokenCounterView view) {
        final Map<String, Integer> oldResult = this.result;
        this.result = new HashMap<>();
//        System.out.println("reductionQueueList");
//        System.out.println((this.reductionQueueList).toString());
        for (ConcurrentLinkedQueue<KeyValue<String, Integer>> reductionQueue : this.reductionQueueList) {
            for (KeyValue<String, Integer> keyValues: reductionQueue) {
                final String key = keyValues.getKey();
                final Integer value = keyValues.getValue();
                if (result.containsKey(key)) {
                	final int oldValue = result.get(key);
                	final int newValue = oldValue+value;
                	this.result.put(key, newValue);
                } else {
                	this.result.put(key, value);
                }
                
                System.out.println("1111111" + this.result);
            }
            System.out.println("22222222" + this.result);
        }
        System.out.println("33333333" + this.result);
        final PropertyChangeEvent updateResultEvent = new PropertyChangeEvent(this, "Result",
                oldResult, this.result);
        this.propertyChangeSupport.firePropertyChange(updateResultEvent);
        view.printNotification(updateResultEvent);
    }

    public void updateResult(final TokenCounterView view) {
    	this.initializeStructures();
        this.clearReductionQueues();
        this.startThreads();
        this.unblockSlaveThreads();
        try {
			this.problemSplit();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        this.joiner.join();
        this.mergeIntermediaryResults(view);
    }
    
    public void setInputString(final String newInputString, final TokenCounterView view) {
        final String oldInputString = this.inputString;
		this.inputString = newInputString;
		final PropertyChangeEvent updateInputStringEvent = new PropertyChangeEvent(this, inputStringAsVariable,
		        oldInputString, newInputString);
		this.propertyChangeSupport.firePropertyChange(updateInputStringEvent);
		view.printNotification(updateInputStringEvent);
		this.updateResult(view);
    }

    public void addPropertyChangeListener(final PropertyChangeListener aListener) {
        this.propertyChangeSupport.addPropertyChangeListener(aListener);
    }

    public int getNumThreads() {
        return this.numThreads;
    }

    public TokenCounterBarrier getBarrier() {
        return this.barrier;
    }

    @Override
	public ArrayBlockingQueue<KeyValue<String, Integer>> getKeyValueQueue() {
    	return this.boundedBuffer;
	}


    public BlockingQueue<KeyValue<String, Integer>> getKeyValueQueueForTest() {
    	return this.boundedBuffer;
	}

	public List<ConcurrentLinkedQueue<KeyValue<String, Integer>>> getReductionQueueListForTest() {
        return this.reductionQueueList;
    }
	
    public Joiner getJoiner() {
        return this.joiner;
    }

    public ArrayList<ConcurrentLinkedQueue<KeyValue<String, Integer>>> getReductionQueueList() {
        return this.reductionQueueList;
    }

    public Map<String, Integer> getResult() {
        return this.result;
    }

    public void updateThreads() {
        final List<Thread> oldThreads = this.threads;
        this.joiner = new TokenCounterJoiner(this.numThreads);
        this.barrier = new TokenCounterBarrier(this.numThreads);
        this.threads = new ArrayList<>(this.numThreads);
        for (int i = 0; i < this.numThreads; i++) {
            final Slave slave = new Slave(i, this);
            this.slaves.add(slave);
            final Thread newThread = new Thread(slave, "Slave" + i);
            this.threads.add(newThread);
            final ConcurrentLinkedQueue<KeyValue<String, Integer>> reductionQueue = new ConcurrentLinkedQueue<>();
            this.reductionQueueList.add(reductionQueue);
        }
        final PropertyChangeEvent updateThreadsEvent = new PropertyChangeEvent(this, "Threads",
                oldThreads, this.threads);
        this.propertyChangeSupport.firePropertyChange(updateThreadsEvent);
    }

    public void setNumThreads(final int newNumThreads, final TokenCounterView view) {
        final int oldNumThreads = this.numThreads;
        this.numThreads = newNumThreads;
        final PropertyChangeEvent setNumThreadsEvent = new PropertyChangeEvent(this, "NumThreads",
                oldNumThreads, numThreads);
        this.propertyChangeSupport.firePropertyChange(setNumThreadsEvent);
        this.updateThreads();
    }

    public void interruptThreads() {
        for (int i = 0; i < this.numThreads; i++) {
            this.slaves.get(i).signalQuit();
            this.threads.get(i).interrupt();
        }
    }
    
    @Override
    public String toString() {
        return AMapReduceTracer.MODEL;
    }

}