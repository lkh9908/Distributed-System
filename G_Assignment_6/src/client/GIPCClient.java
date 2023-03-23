package client;

import java.rmi.RemoteException;

import util.annotations.Tags;
import util.tags.DistributedTags;

@Tags({DistributedTags.CLIENT_REMOTE_INTERFACE, DistributedTags.GIPC})
public interface GIPCClient {
    String CLIENT_NAME="Client ";
    void ReceiveNotification(String name, Object proposal) throws RemoteException;
}
