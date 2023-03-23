package comp533.client;


import java.rmi.Remote;
import java.rmi.RemoteException;


import assignments.util.inputParameters.AnAbstractSimulationParametersBean;
//import comp533.server.RemoteServerInterface;
import stringProcessors.AHalloweenCommandProcessor;
import stringProcessors.HalloweenCommandProcessor;
import util.annotations.Tags;
import util.interactiveMethodInvocation.SimulationParametersControllerFactory;
import util.tags.DistributedTags;
import util.trace.port.consensus.ProposalLearnedNotificationReceived;
import util.trace.port.consensus.ProposedStateSet;
import util.trace.port.consensus.communication.CommunicationStateNames;

@SuppressWarnings("serial")
@Tags({DistributedTags.CLIENT_REMOTE_OBJECT, DistributedTags.RMI, DistributedTags.GIPC})

public class RemoteClientObject extends AnAbstractSimulationParametersBean implements Remote {
	protected ClientInterface serverProxy;

	private void processCommands(final String inputCommand) {
		setBroadcastMetaState(true);
        setIPCMechanism(ipcMechanism);
		final HalloweenCommandProcessor processor = new AHalloweenCommandProcessor();
		processor.processCommand(null);
		
	}

}
