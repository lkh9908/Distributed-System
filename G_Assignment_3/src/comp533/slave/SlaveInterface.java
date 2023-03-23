package comp533.slave;

import comp533.client.ClientInterface;
import comp533.keyValue.KeyValueInterface;
import comp533.reducer.ReducerInterface;

import java.util.List;
import java.util.Map;

public interface SlaveInterface extends Runnable {
    void notifySlave();
    void splitBoundedBuffer() throws InterruptedException;
    Map<String, Integer> reduceList(ReducerInterface<String, Integer> reducer, List<KeyValueInterface<String, Integer>> keyValuePairs);
    void signalQuit();
    void setClient(ClientInterface client);
}
