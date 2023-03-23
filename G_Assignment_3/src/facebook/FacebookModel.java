package facebook;

import comp533.barrier.BarrierInterface;
import comp533.barrier.Barrier;
import comp533.client.ClientInterface;
import comp533.joiner.JoinerInterface;
import comp533.keyValue.KeyValueInterface;
import comp533.main.RemoteModel;
import comp533.keyValue.KeyValue;
import comp533.joiner.Joiner;
import comp533.mapper.MapperInterface;
import comp533.main.ModelInterface;
import comp533.mapper.Mapper;
import comp533.slave.SlaveInterface;
import comp533.slave.Slave;
import comp533.view.ViewInterface;
import gradingTools.comp533s19.assignment0.AMapReduceTracer;
import util.trace.Tracer;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import java.rmi.RemoteException;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class FacebookModel extends AMapReduceTracer implements ModelInterface, RemoteModel {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
    private Map<String, List<String>> result;
    private int numThreads;
    private List<Thread> threads;
    private List<FacebookSlave> slaves;
    private String inputString;
    private ArrayBlockingQueue<KeyValueInterface<String, List<String>>> boundedBuffer;
    private ArrayList<ConcurrentLinkedQueue<KeyValueInterface<String, List<String>>>> reductionQueueList;
    private JoinerInterface joiner;
    private BarrierInterface barrier;
    private List<ClientInterface> registeredClients;
    private Stack<FacebookSlave> unassignedSlaves;
    private Stack<ClientInterface> unassignedClients;


    public FacebookModel() {
        this.inputString = null;
        this.numThreads = 0;
        this.reductionQueueList = new ArrayList<>();
        this.slaves = new ArrayList<>();
        this.registeredClients = new ArrayList<>();
        this.unassignedSlaves = new Stack<>();
        this.unassignedClients = new Stack<>();
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        this.pcs.addPropertyChangeListener(listener);
    }

    public String getInputString() {
        return this.inputString;
    }

    public void matchSlavesToClients() {
        if (!this.unassignedSlaves.isEmpty() && !this.unassignedClients.isEmpty()) {
            
            for (int i = 0; i < 2; i++) {
            	System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
                System.out.println(unassignedSlaves);
                System.out.println(unassignedClients);
                System.out.println("################################");
                
            	SlaveInterface unassignedSlave = this.unassignedSlaves.pop();
                ClientInterface unassignedClient = this.unassignedClients.pop();
            	
                unassignedSlave.setClient(unassignedClient);
            }
            
        } else {
        	System.out.println("&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&");
        }
    }

    private void clearReductionQueues() {
        for (ConcurrentLinkedQueue<KeyValueInterface<String, List<String>>> reductionQueue: reductionQueueList) {
            reductionQueue.clear();
        }
    }

    private void unblockSlaveThreads() {
        for (int i = 0; i < this.numThreads; i++) {
            SlaveInterface currentSlave = this.slaves.get(i);
            currentSlave.notifySlave();
        }
    }

    private void initializeStructures() {
        this.result = new HashMap<>();
        this.boundedBuffer = new ArrayBlockingQueue<>(this.BUFFER_SIZE);
        for (ConcurrentLinkedQueue<KeyValueInterface<String, List<String>>> reductionQueue: this.reductionQueueList) {
            reductionQueue.clear();
        }
    }

    private void produceBoundedBuffer(KeyValueInterface<String, List<String>> keyValue) throws InterruptedException {
        this.traceEnqueueRequest(keyValue);
        this.boundedBuffer.put(keyValue);
        this.traceEnqueue(this.boundedBuffer);
    }

    private void endEnqueue() {
        try {
            for (int i = 0; i < this.numThreads; i++) {
                KeyValueInterface<String, List<String>> endKeyValue = new KeyValue<>(null, null);
                this.traceEnqueueRequest(endKeyValue);
                this.boundedBuffer.put(endKeyValue);
            }
        } catch (InterruptedException ex) {
            Tracer.error(ex.getMessage());
        }
    }

    private void startThreads() {
        for (Thread currentThread: this.threads) {
            if (currentThread.getState() == Thread.State.NEW) {
                currentThread.start();
            }
        }
    }

    private void problemSplit() throws InterruptedException {
        String[] tokens = this.inputString.split(" ");
        FacebookMapper mapper = new FacebookMapper();
        for (String token: tokens) {
        	String[] all = token.split(",");
        	String user = all[0];
        	String[] friends = Arrays.copyOfRange(all, 1, all.length);
        	Arrays.sort(friends);
        	KeyValueInterface<String, List<String>> keyValue;
        	System.out.println("()()()()()()()()()()(()(())(()())()()(())(()()(");
        	this.traceRemoteList(friends);
        	System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++");
        	for (String friend: friends) {
        		keyValue = mapper.map(token, user, friend, friends);
        		this.produceBoundedBuffer(keyValue);
     
            }
            
        }
        this.endEnqueue();
        

    }

    private void mergeIntermediaryResults(ViewInterface view) {
        Map<String, List<String>> oldResult = this.result;
        this.result = new HashMap<>();
        for (ConcurrentLinkedQueue<KeyValueInterface<String, List<String>>> reductionQueue : this.reductionQueueList) {
            for (KeyValueInterface<String, List<String>> keyValues: reductionQueue) {
            	final String key = keyValues.getKey();
                final List<String> value = keyValues.getValue();
                if (this.result.get(key) == null) {
                	this.result.put(key, value);
                } else {
                    final List<String> current = this.result.get(key);
                    
    				final List<String> newValue = intersection(current, value);
    				this.result.put(key, newValue);
                }
            }
        }
        this.traceRemoteResult(this.result);
        PropertyChangeEvent updateResultEvent = new PropertyChangeEvent(this, "Result",
                oldResult, this.result);
        this.pcs.firePropertyChange(updateResultEvent);
        view.propertyChange(updateResultEvent);
    }

    public <T> List<T> intersection(List<T> list1, List<T> list2) {
        List<T> list = new ArrayList<T>();

        for (T t : list1) {
            if(list2.contains(t)) {
                list.add(t);
            }
        }

        return list;
    }
    
    private void updateResult(ViewInterface view) throws InterruptedException {
        this.initializeStructures();
        this.clearReductionQueues();
        this.startThreads();
        this.unblockSlaveThreads();
        this.problemSplit();
        this.joiner.join();
        this.mergeIntermediaryResults(view);
    }

    public void setInputString(String newInputString, ViewInterface view) {
        try {
            String oldInputString = this.inputString;
            this.inputString = newInputString;
            PropertyChangeEvent updateInputStringEvent = new PropertyChangeEvent(this, "InputString",
                    oldInputString, newInputString);
            this.pcs.firePropertyChange(updateInputStringEvent);
            view.propertyChange(updateInputStringEvent);
            this.updateResult(view);
        } catch (InterruptedException ex) {
            Tracer.error(Arrays.toString(ex.getStackTrace()));
        }
    }

    public int getNumThreads() {
        return this.numThreads;
    }

    public BarrierInterface getBarrier() {
        return this.barrier;
    }

    public ArrayBlockingQueue<KeyValueInterface<String, List<String>>> getBoundedBuffer() {
        return this.boundedBuffer;
    }

    public JoinerInterface getJoiner() {
        return this.joiner;
    }

    public ArrayList<ConcurrentLinkedQueue<KeyValueInterface<String, List<String>>>> getReductionQueueList() {
        return this.reductionQueueList;
    }

    public Map<String, List<String>> getResult() {
        return this.result;
    }

    public void updateThreads() {
        List<Thread> oldThreads = this.threads;
        this.joiner = new Joiner(this.numThreads);
        this.barrier = new Barrier(this.numThreads);
        this.threads = new ArrayList<>(this.numThreads);
        for (int i = 0; i < this.numThreads; i++) {
            FacebookSlave slave = new FacebookSlave(i, this);
            this.slaves.add(slave);
            this.unassignedSlaves.add(slave);
            Thread newThread = new Thread(slave, "Slave" + i);
            this.threads.add(newThread);
            ConcurrentLinkedQueue<KeyValueInterface<String, List<String>>> reductionQueue = new ConcurrentLinkedQueue<>();
            this.reductionQueueList.add(reductionQueue);
        }
        this.matchSlavesToClients();
        PropertyChangeEvent updateThreadsEvent = new PropertyChangeEvent(this, "Threads",
                oldThreads, this.threads);
        this.pcs.firePropertyChange(updateThreadsEvent);
    }

    public void registerClient(ClientInterface clientToRegister) {
        this.traceRegister(clientToRegister);
        this.unassignedClients.add(clientToRegister);
        this.registeredClients.add(clientToRegister);
    }

    public void callClientQuit() throws RemoteException {
        for (ClientInterface registeredClient: registeredClients) {
            registeredClient.quit();
        }
    }

    public void interruptThreads() {
        for (int i = 0; i < this.numThreads; i++) {
            this.slaves.get(i).signalQuit();
            this.threads.get(i).interrupt();
        }
    }

    public void setNumThreads(int numThreads, ViewInterface view) {
        int oldNumThreads = this.numThreads;
        this.numThreads = numThreads;
        PropertyChangeEvent setNumThreadsEvent = new PropertyChangeEvent(this, "NumThreads",
                oldNumThreads, numThreads);
        this.pcs.firePropertyChange(setNumThreadsEvent);
        this.updateThreads();
    }

    @Override
    public String toString() {
        return AMapReduceTracer.MODEL;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof FacebookModel)) {
            return false;
        }
        FacebookModel otherCounter = (FacebookModel) obj;
        return this.getResult() == otherCounter.getResult();
    }
}
