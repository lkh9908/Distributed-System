package comp533.main;

import comp533.client.ClientInterface;
import comp533.view.ViewInterface;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RemoteModel extends Remote, Serializable {
    void registerClient(ClientInterface clientToRegister) throws RemoteException;
    void setNumThreads(int numThreads, ViewInterface view) throws RemoteException;
    void setInputString(String newInputString, ViewInterface view) throws RemoteException;
    void interruptThreads() throws RemoteException;
    void callClientQuit() throws RemoteException;
}
