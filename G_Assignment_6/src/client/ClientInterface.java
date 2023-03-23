package client;

import assignments.util.inputParameters.SimulationParametersListener;
import stringProcessors.HalloweenCommandProcessor;
import util.annotations.Tags;
import util.tags.DistributedTags;

@Tags({DistributedTags.CLIENT_CONFIGURER, DistributedTags.RMI, DistributedTags.GIPC})

public interface ClientInterface extends SimulationParametersListener {
    HalloweenCommandProcessor MYPROCESSOR = null;

    void simulationCommand(String aCommand);
    void lookupServer();

    
	void setTracing();
    void locateRegistry(int port, String host);
    void start(String[] args);
    
    void exportClient();
}
