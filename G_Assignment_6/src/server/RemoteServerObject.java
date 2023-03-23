package server;

import java.io.Serializable;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

import assignments.util.inputParameters.AnAbstractSimulationParametersBean;

import stringProcessors.AHalloweenCommandProcessor;
import stringProcessors.HalloweenCommandProcessor;
import util.annotations.Tags;
import util.tags.DistributedTags;
import util.trace.factories.FactoryTraceUtility;
import util.trace.misc.ThreadDelayed;
import util.trace.port.consensus.ConsensusTraceUtility;
import util.trace.port.consensus.ProposalLearnedNotificationSent;
import util.trace.port.consensus.RemoteProposeRequestReceived;
import util.trace.port.consensus.communication.CommunicationStateNames;
import util.trace.port.nio.NIOTraceUtility;
import util.trace.port.rpc.rmi.RMIObjectLookedUp;
import util.trace.port.rpc.rmi.RMIObjectRegistered;
import util.trace.port.rpc.rmi.RMIRegistryLocated;
import util.trace.port.rpc.rmi.RMITraceUtility;


@SuppressWarnings("serial")
@Tags({DistributedTags.SERVER_REMOTE_OBJECT, DistributedTags.RMI, DistributedTags.GIPC})

public class RemoteServerObject extends AnAbstractSimulationParametersBean implements Remote {
	/**
	 * 
	 */
//	private static final long serialVersionUID = 1L;
    private Registry rmiRegistry;
//	private List<ClientInterface> clients;

//	@Override
//	public void setTracing() {
//        FactoryTraceUtility.setTracing();
//        RMITraceUtility.setTracing();
//        ConsensusTraceUtility.setTracing();
//        NIOTraceUtility.setTracing();
//        ThreadDelayed.enablePrint();
//        this.trace(true);
//    }

	
//	public void findRegistry(int registryPort, String registryHost) throws RemoteException {
//        this.rmiRegistry = LocateRegistry.getRegistry(registryPort);
//        RMIRegistryLocated.newCase(this, registryHost, registryPort, this.rmiRegistry);
//        setBroadcastMetaState(true);
//        setIPCMechanism(ipcMechanism);
//    }

//    @Override
//    public void exportServer(int serverPort) throws RemoteException {
//        UnicastRemoteObject.exportObject(this, serverPort);
//        this.rmiRegistry.rebind(RemoteServerInterface.class.getName(), this);
//        RMIObjectRegistered.newCase(this, RemoteServerInterface.class.getName(), this, rmiRegistry);
//    }


	
//	@Override
//    public void receiveRequest(String aCommand, RemoteClientInterface clientProxy) {
//        RemoteProposeRequestReceived.newCase(this, CommunicationStateNames.COMMAND, -1, aCommand);
//        for (RemoteClientInterface aClientProxy: this.clients) {
//            if (!aClientProxy.equals(clientProxy)) {
//                try {
//                    ProposalLearnedNotificationSent.newCase(this, CommunicationStateNames.BROADCAST_MODE, -1, aCommand);
//                    boolean atomicBroadcastStatus = this.isAtomicBroadcast();
//                    aClientProxy.receiveProposalLearnedNotification(aCommand, atomicBroadcastStatus);
//                } catch (RemoteException ex) {
//                    ex.printStackTrace();
//                }
//            }
//        }
//
//    }
	
//	@Override
//	public void registerClients() {
//		processCommands("haofan", "zhenfan");
//    }
	
	@Override
    public void quit(int aCode) {
        System.exit(aCode);
    }
	
	private void processCommands(String inputCommand, String anotherCommand) {
		setBroadcastMetaState(true);
        setIPCMechanism(ipcMechanism);
		HalloweenCommandProcessor processor = new AHalloweenCommandProcessor();
		processor.processCommand(null);
		
	}





}
