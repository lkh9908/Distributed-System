package comp533.client;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;

import comp533.keyValue.KeyValueInterface;

public interface ClientInterface extends Remote, Serializable {
    Map<String, Integer> reduce(List<KeyValueInterface<String, Integer>> serializableKeyValuePairs) throws RemoteException;
    void quit() throws RemoteException;
}
