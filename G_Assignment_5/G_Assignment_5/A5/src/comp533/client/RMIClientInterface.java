package comp533.client;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;

import util.annotations.Tags;
import util.tags.DistributedTags;

@Tags({DistributedTags.CLIENT_REMOTE_INTERFACE, DistributedTags.RMI})


public interface RMIClientInterface extends Remote, Serializable {
	void quit(int aCode) throws RemoteException;
    void receiveNotificationsViaRMI(String object, Object newProposal) throws RemoteException;
    
}
