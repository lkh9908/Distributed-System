package server;



import util.annotations.Tags;
import util.tags.DistributedTags;

@Tags({DistributedTags.SERVER_REMOTE_INTERFACE, DistributedTags.GIPC})

public interface GIPCServer {
    void registerGIPCClients();
//    void receiveCommandViaGIPC(String aCommand, GIPCClient clientProxy);
}
