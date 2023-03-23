package comp533.server;

import util.interactiveMethodInvocation.IPCMechanism;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;

import comp533.client.RMIClientInterface;
import util.annotations.Tags;
import util.tags.DistributedTags;

@Tags({DistributedTags.SERVER_REMOTE_INTERFACE, DistributedTags.RMI})

public interface RMIServerInterface extends Remote, Serializable {
	void registerRMIClients() throws RemoteException;
    void receiveCommandViaRMI(String aCommand, RMIClientInterface clientProxy) throws RemoteException;
    void receiveIPCMechanismViaRMI(IPCMechanism newValue, RMIClientInterface clientProxy) throws RemoteException;
    
    void setTracing() throws RemoteException;
    void locateRegistry(int registryPort, String registryHost) throws RemoteException;
    void exportServerProxy(int serverPort) throws RemoteException;
    void quit(int aCode) throws RemoteException;
    
}
