package comp533.server;


import comp533.client.GIPCClient;
import util.annotations.Tags;
import util.tags.DistributedTags;

@Tags({DistributedTags.SERVER_REMOTE_INTERFACE, DistributedTags.GIPC})

public interface GIPCServer {
    void registerGIPCClients();
    void receiveCommandViaGIPC(String aCommand, GIPCClient clientProxy);
}
