package comp533.client;

import assignments.util.inputParameters.SimulationParametersListener;
import stringProcessors.HalloweenCommandProcessor;
import util.annotations.Tags;
import util.tags.DistributedTags;

@Tags({DistributedTags.CLIENT_CONFIGURER, DistributedTags.RMI, DistributedTags.GIPC})

public interface ClientInterface extends SimulationParametersListener {
    HalloweenCommandProcessor Processor = null;
	void setTracing();
    void locateRegistry(int port, String host);
    void start(String[] args);
    void simulationCommand(String aCommand);
    void lookupServerProxy();
    void exportClientProxy();
}
