package client;

import util.annotations.Tags;
import util.tags.DistributedTags;
import util.trace.port.consensus.RemoteProposeRequestSent;
import util.trace.trickOrTreat.LocalCommandObserved;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.rmi.RemoteException;

import coupledsims.AStandAloneTwoCoupledHalloweenSimulations;
import server.remote.ServerInterfaceGipcRmi;
import server.remote.ServerInterfaceRmi;

@Tags({DistributedTags.CLIENT_OUT_COUPLER, DistributedTags.RMI, DistributedTags.GIPC, DistributedTags.NIO})
public class ClientOutCoupler implements PropertyChangeListener{
	String oldName;
	
	ServerInterfaceGipcRmi watcherServer;
	ClientInterfaceGipc thatClient;
//	private static ServerInterface thisServer = new RMIAndGIPCServer();
////  private List<GIPCClient> clients;
////  private GIPCRegistry registry;
	
	int aProposalNumber = 0;
	boolean nio = false;
	@Override
	public void propertyChange(final PropertyChangeEvent anEvent) {
	
		if (!anEvent.getPropertyName().equals("InputString")) {
			return;
		}
		
		final String thisCommand = (String) anEvent.getNewValue();
		LocalCommandObserved.newCase(this, thisCommand);
		if (nio) {
			return;
		}
		
		final AStandAloneTwoCoupledHalloweenSimulations placeHolder = new AStandAloneTwoCoupledHalloweenSimulations();
		placeHolder.getIPCMechanism();
		
		RemoteProposeRequestSent.newCase(thatClient, oldName, aProposalNumber, thisCommand);
		try {
			
			watcherServer.broadcast(thisCommand, thatClient, aProposalNumber);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		aProposalNumber++;
	
	}
	public ClientOutCoupler (final ServerInterfaceGipcRmi anwatcherServer, final ClientInterfaceGipc aClient, final String aClientName, Boolean thisNio) {
		watcherServer = anwatcherServer;
		thatClient = aClient;
		oldName = aClientName;
		

//	        this.setTracing();
//	        super.init(arguments);
//	        
//	        String hostName = ClientArgsProcessor.getServerHost(arguments);
//	        final int port = ClientArgsProcessor.getGIPCPort(arguments);
//	        
//	        this.locateGIPCRegistry(port, hostName);
//	        this.lookupGIPCServerProxy();
	//
	}
	
	

}
