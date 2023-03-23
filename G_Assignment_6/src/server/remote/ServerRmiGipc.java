package server.remote;

import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

import assignments.util.mainArgs.ClientArgsProcessor;
import assignments.util.mainArgs.ServerArgsProcessor;
import client.ClientOutCoupler;
import client.ClientInterfaceGipc;
import client.ClientInterfaceRmi;
import client.ClientRmiGipc;
import util.annotations.Tags;
import util.interactiveMethodInvocation.IPCMechanism;
import util.interactiveMethodInvocation.SimulationParametersControllerFactory;
import util.misc.ThreadSupport;
import util.tags.DistributedTags;

import inputport.rpc.GIPCLocateRegistry;
import inputport.rpc.GIPCRegistry;
import port.ATracingConnectionListener;
import util.trace.bean.BeanTraceUtility;
import util.trace.factories.FactoryTraceUtility;
import util.trace.misc.ThreadDelayed;
import util.trace.port.PortTraceUtility;
import util.trace.port.consensus.ConsensusTraceUtility;
import util.trace.port.consensus.ProposalLearnedNotificationSent;
import util.trace.port.consensus.ProposedStateSet;
import util.trace.port.consensus.RemoteProposeRequestReceived;
import util.trace.port.rpc.rmi.RMIRegistryLocated;
import util.trace.port.nio.NIOTraceUtility;
import util.trace.port.rpc.rmi.RMITraceUtility;
import util.trace.port.rpc.gipc.GIPCObjectRegistered;
import util.trace.port.rpc.gipc.GIPCRPCTraceUtility;
import util.trace.port.rpc.gipc.GIPCRegistryCreated;
import util.trace.port.rpc.rmi.RMIObjectRegistered;

@Tags({ DistributedTags.SERVER_REMOTE_OBJECT, DistributedTags.RMI, DistributedTags.GIPC })
public class ServerRmiGipc extends ServerObjectRmiGipc implements ServerInterfaceGipcRmi {

	List<ClientInterfaceGipc> clientListGIPC = new ArrayList<ClientInterfaceGipc>();
	List<ClientInterfaceGipc> clientListRMI = new ArrayList<ClientInterfaceGipc>();

	private static String RMI_SERVER_HOST_NAME;
	private static int RMI_SERVER_PORT;
	public static String SERVER_NAME;
	private static int NIO_SERVER_PORT;

	// A5
	private static int GIPC_SERVER_PORT;
	protected static GIPCRegistry gipcRegistry;

	// int aProposalNumber = 0;

	@Override
	public void processArgs(String[] args) {
		

		// Make sure you set this property when processing args
		//System.setProperty("java.awt.headless", ClientArgsProcessor.getHeadless(args));
		

		RMI_SERVER_HOST_NAME = ServerArgsProcessor.getRegistryHost(args);
		RMI_SERVER_PORT = ServerArgsProcessor.getRegistryPort(args);
		SERVER_NAME = "SERVER";
		GIPC_SERVER_PORT = ServerArgsProcessor.getGIPCServerPort(args);
		NIO_SERVER_PORT = ServerArgsProcessor.getNIOServerPort(args);
		// ClientArgsProcessor.getServerHost(args);
		System.out.println("RMI_SERVER_HOST_NAME: "+RMI_SERVER_HOST_NAME);
		System.out.println("RMI_SERVER_PORT: "+RMI_SERVER_PORT);
		System.out.println("GIPC_SERVER_PORT: "+GIPC_SERVER_PORT);
		System.out.println("NIO_SERVER_PORT: "+NIO_SERVER_PORT);
		

	}

	@Override
	public void registerClientGIPC(ClientInterfaceGipc aClient) {
		
		clientListGIPC.add(aClient);
		System.out.println("Client registered GIPC");
		System.out.println(aClient);
		System.out.println(clientListGIPC);
	}
	
	@Override
	public void registerClientRMI(ClientInterfaceGipc aClient) {
		
		clientListRMI.add(aClient);
		System.out.println("Client registered RMI");
		System.out.println(aClient);
		System.out.println(clientListRMI);
	}

	@Override
	public void broadcast(String aNewCommand, ClientInterfaceGipc originalClient, int aProposalNumber){
		List<ClientInterfaceGipc> clientList = clientListGIPC;
		
		// TODO Check is this is where delay is needed
		long aDelay = getDelay();
		if (aDelay > 0) {
			ThreadSupport.sleep(aDelay);
		}

		System.out.println("Command recieved for broadcast: " + aNewCommand);
		RemoteProposeRequestReceived.newCase(this, SERVER_NAME, aProposalNumber, aNewCommand);
		ProposalLearnedNotificationSent.newCase(this, SERVER_NAME, aProposalNumber, aNewCommand);
		
		if(clientListGIPC.isEmpty()) {
			clientList = clientListRMI;
			System.out.println("USING RMI IN SERVER");
		}
		else {
			clientList = clientListGIPC;
			System.out.println("USING GIPC IN SERVER");
		}
		System.out.println(clientList);
		System.out.println(clientList.size());
		for (ClientInterfaceGipc client : clientList) {
			System.out.println(client);
			if (client.equals(originalClient)) {
				if (aNewCommand.charAt(0) == 'q') {
					// Need to quit
					try {
						client.quit(0);
					} catch (RemoteException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				continue;
			}
			
			if (aNewCommand.charAt(0) == 'q') {

				try {
					client.quit(0);
				} catch (RemoteException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				continue;
			}

			try {
				client.inCoupler(aNewCommand, aProposalNumber);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//ProposalLearnedNotificationSent.newCase(this, SERVER_NAME, aProposalNumber, aNewCommand);

			//if (aNewCommand.charAt(0) == 'q') {
				// Need to quit
			//	this.quit(0);
			//}
		}
		if (aNewCommand.charAt(0) == 'q') {
			// Need to quit
			this.quit(0);
		}

	}

	@Override
	public void broadcastIPCMechanism(IPCMechanism mechanism, ClientInterfaceGipc originalClient, int aProposalNumber, boolean broadcast) {
		List<ClientInterfaceGipc> clientList;
		
		// TODO Check is this is where delay is needed
		long aDelay = getDelay();
		if (aDelay > 0) {
			ThreadSupport.sleep(aDelay);
		}

		System.out.println("IPC Mechanism recieved for broadcast: " + mechanism);
		setIPCMechanism(mechanism);
		setBroadcastMetaState(broadcast);
		
		

		if(clientListGIPC.isEmpty()) {
			clientList = clientListRMI;
			System.out.println("USING RMI IN SERVER");
		}
		else {
			clientList = clientListGIPC;
			System.out.println("USING GIPC IN SERVER");
		}
		
		if (broadcast) {
			System.out.println("Broadcasting IPC mechanism: "+mechanism);
			RemoteProposeRequestReceived.newCase(this, SERVER_NAME, aProposalNumber, mechanism);
			ProposalLearnedNotificationSent.newCase(this, SERVER_NAME, aProposalNumber, mechanism);
			for (ClientInterfaceGipc client : clientList) {
				if (client.equals(originalClient)) {
					continue;
				}

				try {
					client.makeChangeIpcm(mechanism, aProposalNumber);
					System.out.println("SEND NEW MECHANISM TO A CLIENT FROM SERVER");
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				//ProposalLearnedNotificationSent.newCase(this, SERVER_NAME, aProposalNumber, mechanism);
			}
		}

	}

	@Override
	protected void setTracing() {
		//A6
		NIOTraceUtility.setTracing();
		FactoryTraceUtility.setTracing();
		BeanTraceUtility.setTracing();
		RMITraceUtility.setTracing();
		ConsensusTraceUtility.setTracing();
		ThreadDelayed.enablePrint();
		GIPCRPCTraceUtility.setTracing();

		
		// A5
		FactoryTraceUtility.setTracing();
		BeanTraceUtility.setTracing();
		RMITraceUtility.setTracing();
		ConsensusTraceUtility.setTracing();
		ThreadDelayed.enablePrint();
		GIPCRPCTraceUtility.setTracing();
		NIOTraceUtility.setTracing();

		// A4
		PortTraceUtility.setTracing();
		RMITraceUtility.setTracing();
		NIOTraceUtility.setTracing();
		FactoryTraceUtility.setTracing();
		ConsensusTraceUtility.setTracing();
		ThreadDelayed.enablePrint();
		trace(true);
	}

	@Override
	protected void init(String[] args) {

		setTracing();

		this.processArgs(args);
		createGIPCRegistry();

		try {
			final Registry rmiRegistry = LocateRegistry.getRegistry(RMI_SERVER_HOST_NAME, RMI_SERVER_PORT);
			RMIRegistryLocated.newCase(this, RMI_SERVER_HOST_NAME, RMI_SERVER_PORT, rmiRegistry);
			// Create remote server object

			// create proxy of remote server object
			// UnicastRemoteObject.exportObject(server, 0);
			UnicastRemoteObject.exportObject(this, 0);
			// send server to RMI server
			// rmiRegistry.rebind(SERVER_NAME, server);
			rmiRegistry.rebind(SERVER_NAME, this);

			RMIObjectRegistered.newCase(this, SERVER_NAME, (ServerInterfaceRmi) this, rmiRegistry);

			System.out.println("Server proxy sent to RMI Registry");

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public void start(String[] args) {
		init(args);
		// register a callback to process actions denoted by the user commands
		SimulationParametersControllerFactory.getSingleton().addSimulationParameterListener(this);
		// use the calling back library
		SimulationParametersControllerFactory.getSingleton().processCommands();		
		//init(args);

	}

	@Override
	public void fakeMethod(String stringOne, String stringTwo) {
		// TODO Auto-generated method stub

	}

	@Override
	public void fakeMethodTwo(String stringOne, ClientInterfaceRmi client) {
		// TODO Auto-generated method stub

	}
	
	@Override
	public void fakeMethodThree(String stringOne, ClientInterfaceGipc client) {
		// TODO Auto-generated method stub

	}
	
	@Override
	public void fakeMethodFour(String stringOne, boolean trueFalse) {
		// TODO Auto-generated method stub

	}
	
	@Override
	public void fakeMethodFive(String stringOne, IPCMechanism mechanism) {
		// TODO Auto-generated method stub

	}
	
	@Override
	public void createGIPCRegistry() {
		System.out.println("GIPC_SERVER_PORT: ");
		System.out.println(GIPC_SERVER_PORT);
		gipcRegistry = GIPCLocateRegistry.createRegistry(GIPC_SERVER_PORT);
		GIPCRegistryCreated.newCase(this, GIPC_SERVER_PORT);

		final ServerInterfaceRmi server = new ServerRmiGipc();
		gipcRegistry.rebind(SERVER_NAME, server);
		GIPCObjectRegistered.newCase(this, SERVER_NAME, this, gipcRegistry);
		gipcRegistry.getInputPort().addConnectionListener(new ATracingConnectionListener(gipcRegistry.getInputPort()));
		System.out.println("ADDED CONNECTION LISTENER");
	}

	@Override
	public void ipcMechanism(IPCMechanism mechanism) {
		setIPCMechanism(mechanism);
		
		if(this.broadcastMetaState) {
			int aProposalNumber = -1;
			ClientInterfaceGipc fake = new ClientRmiGipc();
			broadcastIPCMechanism(mechanism, fake, aProposalNumber , this.broadcastMetaState);
		}
	}

}
