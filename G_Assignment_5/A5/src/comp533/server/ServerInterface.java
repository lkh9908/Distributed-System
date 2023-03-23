package comp533.server;

import assignments.util.inputParameters.SimulationParametersListener;
import comp533.client.ClientInterface;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;

import util.annotations.Tags;
import util.tags.DistributedTags;

@Tags({DistributedTags.SERVER_CONFIGURER, DistributedTags.RMI, DistributedTags.GIPC})

public interface ServerInterface extends SimulationParametersListener {
    void setTracing();
    void start(String[] args);
}
