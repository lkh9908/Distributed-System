package client;

import util.annotations.Tags;
import util.interactiveMethodInvocation.IPCMechanism;
import util.misc.ThreadSupport;
import util.tags.DistributedTags;

import java.beans.PropertyChangeListener;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;


import assignments.util.mainArgs.ClientArgsProcessor;
//import comp533.client.GIPCClient;
import coupledsims.AStandAloneTwoCoupledHalloweenSimulations;
import coupledsims.Simulation;
import coupledsims.Simulation1;
import inputport.rpc.GIPCLocateRegistry;
import inputport.rpc.GIPCRegistry;
import main.BeauAndersonFinalProject;
import server.remote.ServerInterfaceRmi;
import server.remote.ServerRmiGipc;
import server.remote.ServerInterfaceGipcRmi;
import stringProcessors.HalloweenCommandProcessor;
import util.trace.bean.BeanTraceUtility;
import util.trace.factories.FactoryTraceUtility;
import util.trace.misc.ThreadDelayed;
import util.trace.port.PortTraceUtility;
import util.trace.port.consensus.ConsensusTraceUtility;
import util.trace.port.consensus.ProposalLearnedNotificationReceived;
import util.trace.port.consensus.ProposalLearnedNotificationSent;
import util.trace.port.consensus.ProposedStateSet;
import util.trace.port.consensus.communication.CommunicationStateNames;
import util.trace.port.nio.NIOTraceUtility;
import util.trace.port.rpc.gipc.GIPCObjectLookedUp;
import util.trace.port.rpc.gipc.GIPCRPCTraceUtility;
import util.trace.port.rpc.gipc.GIPCRegistryLocated;
import util.trace.port.rpc.rmi.RMIObjectLookedUp;
import util.trace.port.rpc.rmi.RMIRegistryLocated;
import util.trace.port.rpc.rmi.RMITraceUtility;

@Tags({ DistributedTags.CLIENT_REMOTE_OBJECT, DistributedTags.RMI, DistributedTags.GIPC })
public class ClientRmiGipc extends AStandAloneTwoCoupledHalloweenSimulations implements ClientInterfaceRmi, ClientInterfaceGipc {
	HalloweenCommandProcessor commandProcessor;
	protected int NUM_EXPERIMENT_COMMANDS = 500;
	public static final String customCommandMove = "move 1 -1";
	public static final String customCommandUndo = "undo";

	

	private static String serverHost;
	private static int serverPort;
	private static String SERVER_NAME;
	public String CLIENT_NAME;
	
	protected PropertyChangeListener simulationCoupler;
	ServerInterfaceGipcRmi server = null;
	ServerInterfaceGipcRmi serverGIPC = null;
	
	private static int GIPC_SERVER_PORT;
	protected static GIPCRegistry gipcRegistry;
	private static String GIPC_SERVER_NAME ;
	protected boolean broadcastIPCMechanism = false;

	public int aProposalNumber;
	
	PropertyChangeListener clientOutCoupler;

	

	@Override
	public void argumentProcessor(String[] args) {
	
		System.setProperty("java.awt.headless", ClientArgsProcessor.getHeadless(args));

		serverHost = ClientArgsProcessor.getRegistryHost(args);
		serverPort = ClientArgsProcessor.getRegistryPort(args);
		SERVER_NAME = "SERVER";
		
		CLIENT_NAME = ClientArgsProcessor.getClientName(args);



		GIPC_SERVER_PORT = ClientArgsProcessor.getGIPCPort(args);
		GIPC_SERVER_NAME = ClientArgsProcessor.getServerHost(args);
		
	}
	
	@Override
	protected void setTracing() {
	
		NIOTraceUtility.setTracing();
		FactoryTraceUtility.setTracing();
		BeanTraceUtility.setTracing();
		RMITraceUtility.setTracing();
		ConsensusTraceUtility.setTracing();
		ThreadDelayed.enablePrint();
		GIPCRPCTraceUtility.setTracing();

		

	}

	@Override
	public void init(String[] args) {
		setTracing();

		this.argumentProcessor(args);
		commandProcessor = createSimulation(Simulation1.SIMULATION1_PREFIX);
		
		gipcRegistry = GIPCLocateRegistry.getRegistry(GIPC_SERVER_NAME, GIPC_SERVER_PORT, CLIENT_NAME);
		GIPCRegistryLocated.newCase(this, GIPC_SERVER_NAME, GIPC_SERVER_PORT, CLIENT_NAME);
		
		serverGIPC = (ServerInterfaceGipcRmi) gipcRegistry.lookup(ServerRmiGipc.class, SERVER_NAME);
		GIPCObjectLookedUp.newCase(this, serverGIPC, ServerRmiGipc.class, SERVER_NAME, gipcRegistry);
		
		try {
			serverGIPC.registerClientGIPC(this);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		
		
		Registry rmiRegistry = null;
		try {
			rmiRegistry = LocateRegistry.getRegistry(serverHost, serverPort);
			RMIRegistryLocated.newCase(this, serverHost, serverPort, rmiRegistry);
		} catch (RemoteException e3) {
			e3.printStackTrace();
		}
		
		try {
			System.out.println("WE ARE LOOKING UP server from RMI REGISTRY HERE: "+SERVER_NAME);
			server = (ServerInterfaceGipcRmi) rmiRegistry.lookup(SERVER_NAME);
			System.out.println("SERVER looked up in RMI registry!!");
			RMIObjectLookedUp.newCase(this, server, SERVER_NAME, rmiRegistry);
		} catch (AccessException e2) {
			e2.printStackTrace();
		} catch (RemoteException e2) {

			e2.printStackTrace();
		} catch (NotBoundException e2) {
			e2.printStackTrace();
		}

		try {
			UnicastRemoteObject.exportObject(this, 0);
		} catch (RemoteException e1) {
			e1.printStackTrace();
		}

		try {
			server.registerClientRMI(this);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		
		
		clientOutCoupler = new ClientOutCoupler(server, this, CLIENT_NAME, false);
		
		commandProcessor.addPropertyChangeListener(clientOutCoupler);
		
		System.out.println("added server as a property change listener of client");
	}

	@Override
	public HalloweenCommandProcessor createSimulation(String aPrefix) {
		return BeauAndersonFinalProject.createSimulation(aPrefix, Simulation1.SIMULATION1_X_OFFSET,
				Simulation.SIMULATION_Y_OFFSET, Simulation.SIMULATION_WIDTH, Simulation.SIMULATION_HEIGHT,
				Simulation1.SIMULATION1_X_OFFSET, Simulation.SIMULATION_Y_OFFSET);
	}
	
	@Override
	public void inCoupler(String aNewCommand, int proposalNumber) {
//		for (GIPCClient otherClientProxy : this.clients) {
//        	
//        	System.out.println("?????????????????????????");
//        	System.out.println(otherClientProxy);
//        	System.out.println(thisClient);
//        	System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!");
//            if (!otherClientProxy.equals(thisClient)) {
//                ProposalLearnedNotificationSent.newCase(this, CommunicationStateNames.BROADCAST_MODE, -1, newCommand);
//                otherClientProxy.NotificationReceiver(CommunicationStateNames.COMMAND, newCommand);
//            }
//        }
		System.out.println("recieved broadcased command: "+ aNewCommand);
		ProposalLearnedNotificationReceived.newCase(this, CLIENT_NAME, proposalNumber, aNewCommand);
		commandProcessor.processCommand(aNewCommand);
		ProposedStateSet.newCase(this, CLIENT_NAME, proposalNumber, aNewCommand);
		System.out.println("executed command");
		System.out.println("A PROPOSAL NUMBER: "+proposalNumber);
		
	}
	

	@Override

	public void simulationCommand(String aCommand) {

		IPCMechanism mechanism = getIPCMechanism();
		System.out.println("IPC Mechanism gotten from old GIPC simiulation command method");
		System.out.println(mechanism);
		
		
		if(mechanism.toString().equals("GIPC")) {
						
			commandProcessor.removePropertyChangeListener(clientOutCoupler);
			clientOutCoupler = new ClientOutCoupler(serverGIPC, this, CLIENT_NAME, false);
			commandProcessor.addPropertyChangeListener(clientOutCoupler);
			System.out.println("using gipc proxy server");
		}
		if(mechanism.toString().equals("RMI")) {
			commandProcessor.removePropertyChangeListener(clientOutCoupler);
			clientOutCoupler = new ClientOutCoupler(server, this, CLIENT_NAME, false);
			commandProcessor.addPropertyChangeListener(clientOutCoupler);
			System.out.println("using RMI proxy server");
		}
		
		ProposedStateSet.newCase(this, CLIENT_NAME, aProposalNumber, mechanism);
		try {
			server.broadcastIPCMechanism(mechanism, this, aProposalNumber, broadcastIPCMechanism);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		commandProcessor.setInputString(aCommand); 
	}
	
	@Override	
	public void quit(int aCode) {
		
		System.exit(aCode);
	}
	
	@Override
	public void localProcessingOnly(boolean newValue) {
		super.localProcessingOnly(newValue);
		if (isLocalProcessingOnly()) {
			commandProcessor.removePropertyChangeListener(clientOutCoupler);
			
		} else {
			commandProcessor.addPropertyChangeListener(clientOutCoupler);
			
		}
	}
	
	
	
	@Override
	public void ipcMechanism(IPCMechanism mechanism) {
		setIPCMechanism(mechanism);
		
		if(this.broadcastMetaState) {
			try {
				server.broadcastIPCMechanism(mechanism, this, aProposalNumber, broadcastIPCMechanism);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Override
	public void processArgs(String[] args) throws RemoteException {
		
		
	}

	@Override
	public void changeIPCMechanism(IPCMechanism mechanism, int proposalNumber) throws RemoteException {
		
		
	}

	@Override
	public void broadcastMetaState(boolean broadcast) {
		broadcastIPCMechanism = broadcast;
		setBroadcastMetaState(broadcast);
	
	}
	
	@Override
	public void makeChangeIpcm(IPCMechanism mechanism, int proposalNumber) {
		ProposalLearnedNotificationReceived.newCase(this, CLIENT_NAME, proposalNumber, mechanism);
		setIPCMechanism(mechanism);
		System.out.print("GOT IPC MECHANISM CHANGE: "+mechanism);
		ProposedStateSet.newCase(this, CLIENT_NAME, proposalNumber, mechanism);
		//aProposalNumber++;
		
	}










}
