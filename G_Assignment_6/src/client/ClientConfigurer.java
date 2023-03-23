package client;

import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.ArrayBlockingQueue;

import assignments.util.MiscAssignmentUtils;
import assignments.util.mainArgs.ClientArgsProcessor;
import coupledsims.Simulation;
import coupledsims.Simulation1;
import inputport.nio.manager.NIOManager;
import inputport.nio.manager.NIOManagerFactory;
import inputport.nio.manager.factories.classes.AConnectCommandFactory;
import inputport.nio.manager.factories.selectors.ConnectCommandFactorySelector;
import inputport.rpc.GIPCLocateRegistry;
import inputport.rpc.GIPCRegistry;
import main.BeauAndersonFinalProject;
import server.remote.ServerInterfaceGipcRmi;
import server.remote.ServerRmiGipc;
import stringProcessors.HalloweenCommandProcessor;
import util.annotations.Tags;
import util.interactiveMethodInvocation.IPCMechanism;
import util.tags.DistributedTags;
import util.trace.bean.BeanTraceUtility;
import util.trace.factories.FactoryTraceUtility;
import util.trace.misc.ThreadDelayed;

import util.trace.port.consensus.ConsensusTraceUtility;
import util.trace.port.consensus.ProposalLearnedNotificationReceived;
import util.trace.port.consensus.ProposedStateSet;
import util.trace.port.consensus.RemoteProposeRequestSent;
import util.trace.port.nio.NIOTraceUtility;
import util.trace.port.rpc.gipc.GIPCObjectLookedUp;
import util.trace.port.rpc.gipc.GIPCRPCTraceUtility;
import util.trace.port.rpc.gipc.GIPCRegistryLocated;
import util.trace.port.rpc.rmi.RMIObjectLookedUp;
import util.trace.port.rpc.rmi.RMIRegistryLocated;
import util.trace.port.rpc.rmi.RMITraceUtility;
import utilRead.ReadThreadInterface;

@Tags({DistributedTags.CLIENT_CONFIGURER, DistributedTags.RMI, DistributedTags.GIPC, DistributedTags.NIO})
public class ClientConfigurer  extends ClientRmiGipc implements ClientInterfaceNio {
	
	protected int NUM_EXPERIMENT_COMMANDS = 500;
	
	HalloweenCommandProcessor commandProcessor;

	
	public static final String EXPERIMENT_COMMAND_2 = "undo";
	
	public static final String EXPERIMENT_COMMAND_1 = "move 1 -1";

	protected PropertyChangeListener simulationCoupler;
	ServerInterfaceGipcRmi server = null;
	ServerInterfaceGipcRmi serverGIPC = null;
	private static String SERVER_NAME;
	private static String CLIENT_NAME;

	private static String RMI_SERVER_HOST_NAME;
	private static int RMI_SERVER_PORT;


	private static int GIPC_SERVER_PORT;
	protected static GIPCRegistry gipcRegistry;
	private static String GIPC_SERVER_NAME ;
	private static boolean broadcastIPCMechanism = false;
	private static int aProposalNumber;
	
	PropertyChangeListener clientOutCoupler;

	@Override
	public HalloweenCommandProcessor createSimulation(final String aPrefix) {
		return BeauAndersonFinalProject.createSimulation(aPrefix, Simulation1.SIMULATION1_X_OFFSET,
				Simulation.SIMULATION_Y_OFFSET, Simulation.SIMULATION_WIDTH, Simulation.SIMULATION_HEIGHT,
				Simulation1.SIMULATION1_X_OFFSET, Simulation.SIMULATION_Y_OFFSET);
	}


	@Override
	public void processArgs(final String[] args) {
	

		// Make sure you set this property when processing args
		System.setProperty("java.awt.headless", ClientArgsProcessor.getHeadless(args));

		RMI_SERVER_HOST_NAME = ClientArgsProcessor.getRegistryHost(args);
		RMI_SERVER_PORT = ClientArgsProcessor.getRegistryPort(args);
		SERVER_NAME = "SERVER";
				//ClientArgsProcessor.getServerHost(args);
		CLIENT_NAME = ClientArgsProcessor.getClientName(args);


		
		GIPC_SERVER_PORT = ClientArgsProcessor.getGIPCPort(args);
		GIPC_SERVER_NAME = ClientArgsProcessor.getServerHost(args);
		
	}
	
//	public void RMIAndGIPCClient() {
//        new int i = 1+1;
//    }
    public static void getSingleton() {
//        return thisClient;
    }
//    @Override
//    protected void init(final String[] arguments) {
//        this.setTracing();
//        super.init(arguments);
//        
//        String hostName = ClientArgsProcessor.getServerHost(arguments);
//        final int port = ClientArgsProcessor.getGIPCPort(arguments);
//        
//        this.locateGIPCRegistry(port, hostName);
//        this.lookupGIPCServerProxy();
//
//		try {
//			this.exportGIPCClientProxy();
//		} catch (RemoteException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//        System.out.println("should be registered");
//        System.out.println(this.proxyServer);
////        System.out.println(thisClient.getClass());
////        System.out.println(thisClient.getClass().getName());
//        System.out.println(this);
//        this.proxyServer.registerGIPCClients();
//    }
    
	@Override
	public void atomicBroadcast(final boolean wellWhatShould) {
		return;
	}
	
	

	@Override
	public void init(final String[] args) {
		setTracing();

		this.processArgs(args);
		commandProcessor = createSimulation(Simulation1.SIMULATION1_PREFIX);
		
		System.out.println("get location and stuff");

		gipcRegistry = GIPCLocateRegistry.getRegistry(GIPC_SERVER_NAME, GIPC_SERVER_PORT, CLIENT_NAME);
		GIPCRegistryLocated.newCase(this, GIPC_SERVER_NAME, GIPC_SERVER_PORT, CLIENT_NAME);
		
		serverGIPC = (ServerInterfaceGipcRmi) gipcRegistry.lookup(ServerRmiGipc.class, SERVER_NAME);
		GIPCObjectLookedUp.newCase(this, serverGIPC, ServerRmiGipc.class, SERVER_NAME, gipcRegistry);
		System.out.println("worked!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");


		try {
			serverGIPC.registerClientGIPC((ClientInterfaceGipc) this);
		} catch (RemoteException e) {
			System.out.println("well this get printed");
			e.printStackTrace();
		}
		

		Registry rmiRegistry = null;
		try {
			rmiRegistry = LocateRegistry.getRegistry(RMI_SERVER_HOST_NAME, RMI_SERVER_PORT);
			RMIRegistryLocated.newCase(this, RMI_SERVER_HOST_NAME, RMI_SERVER_PORT, rmiRegistry);
		} catch (RemoteException e3) {
			e3.printStackTrace();
		}

		
		try {
			server = (ServerInterfaceGipcRmi) rmiRegistry.lookup(SERVER_NAME);
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
			server.registerClientRMI((ClientInterfaceGipc) this);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		
		
		clientOutCoupler = new ClientOutCoupler(server, (ClientInterfaceGipc) this, CLIENT_NAME, false);
		
		commandProcessor.addPropertyChangeListener(clientOutCoupler);
		
		System.out.println("added server as a property change listener of client");
	}

//  @Override
//  protected void init(final String[] arguments) {
//      this.setTracing();
//      super.init(arguments);
//      
//      String hostName = ClientArgsProcessor.getServerHost(arguments);
//      final int port = ClientArgsProcessor.getGIPCPort(arguments);
//      
//      this.locateGIPCRegistry(port, hostName);
//      this.lookupGIPCServerProxy();
//
//		try {
//			this.exportGIPCClientProxy();
//		} catch (RemoteException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//      System.out.println("should be registered");
//      System.out.println(this.proxyServer);
////      System.out.println(thisClient.getClass());
////      System.out.println(thisClient.getClass().getName());
//      System.out.println(this);
//      this.proxyServer.registerGIPCClients();
//  }
	
	@Override
	public void inCoupler(final String aNewCommand, final int proposalNumber) {
		
		System.out.println("recieved broadcased command: "+ aNewCommand);
		ProposalLearnedNotificationReceived.newCase(this, CLIENT_NAME, proposalNumber, aNewCommand);
		commandProcessor.processCommand(aNewCommand);
		ProposedStateSet.newCase(this, CLIENT_NAME, proposalNumber, aNewCommand);
		System.out.println("executed command");
		
	}
	
	@Override
	
	public void simulateCommand(final String aCommand) {
		
		final IPCMechanism mechanism = getIPCMechanism();

		
		
		if(mechanism.toString().equals("GIPC")) {
						
			commandProcessor.removePropertyChangeListener(clientOutCoupler);
			clientOutCoupler = new ClientOutCoupler(serverGIPC, (ClientInterfaceGipc) this, CLIENT_NAME, false);
			commandProcessor.addPropertyChangeListener(clientOutCoupler);
			
		}
		if(mechanism.toString().equals("RMI")) {
			commandProcessor.removePropertyChangeListener(clientOutCoupler);
			clientOutCoupler = new ClientOutCoupler(server, (ClientInterfaceGipc) this, CLIENT_NAME, false);
			commandProcessor.addPropertyChangeListener(clientOutCoupler);
			
		}
		

		ProposedStateSet.newCase(this, CLIENT_NAME, aProposalNumber, mechanism);
		try {
			server.broadcastIPCMechanism(mechanism, (ClientInterfaceGipc) this, aProposalNumber, broadcastIPCMechanism);
		} catch (RemoteException e) {
			
			e.printStackTrace();
		}
		commandProcessor.setInputString(aCommand); // all commands go to the first command window
	}
	
	@Override	
	public void quit(final int aCode) {
		
		
		System.exit(aCode);
	}
	
	@Override
	public void localProcessingOnly(final boolean newValue) {
		super.localProcessingOnly(newValue);
		if (isLocalProcessingOnly()) {
			commandProcessor.removePropertyChangeListener(clientOutCoupler);
			
		} else {
			commandProcessor.addPropertyChangeListener(clientOutCoupler);
			
		}
	}
	
	@Override
	public void broadcastMetaState(final boolean broadcast) {
		broadcastIPCMechanism = broadcast;
		setBroadcastMetaState(broadcast);
		
	}
	
	@Override
	public void changeIPCMechanism(final IPCMechanism mechanism, int aNewProposalNumber) {
		ProposalLearnedNotificationReceived.newCase(this, CLIENT_NAME, aNewProposalNumber, mechanism);
		setIPCMechanism(mechanism);
		ProposedStateSet.newCase(this, CLIENT_NAME, aNewProposalNumber, mechanism);
		aNewProposalNumber++;
	}
	
	protected NIOManager nioManager = NIOManagerFactory.getSingleton();
	int aServerPort;
	protected SocketChannel socketChannel;
	protected boolean broadcastIpcMechanism1 = false;
	
	ArrayBlockingQueue<ByteBuffer> boundedBuffer = new ArrayBlockingQueue<ByteBuffer>(500);
	ReadThreadInterface reader = null;
	Thread readThread = null;
	
	@Override
	public void nioInit(final String[] args) {
		setTracing();
		setFactories();
		
		aServerPort = ClientArgsProcessor.getNIOServerPort(args);
		System.out.println("NIO SERVER PORT: "+aServerPort);
		
		try {
			socketChannel = SocketChannel.open();
			final InetAddress aServerAddress = InetAddress.getByName("localhost");
			
			nioManager.connect(socketChannel, aServerAddress, aServerPort, 
		
					SelectionKey.OP_READ,
					this);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	
		readThread = new Thread(reader);
				
		final String clientName = "client";
		readThread.setName(clientName);
		
		
		readThread.start();
		
		
		super.init(args);
	}
	
	@Override
	public void setFactories() {
		ConnectCommandFactorySelector.setFactory(new AConnectCommandFactory(0));
	}

	

	@Override
	public void socketChannelRead(final SocketChannel argumentone, final ByteBuffer aMessage, final int argumenttwo) {
		
		final ByteBuffer copy = MiscAssignmentUtils.deepDuplicate(aMessage);
		boundedBuffer.add(copy);
		
		reader.notifyThread();	
		
	}
	
	@Override
	public ArrayBlockingQueue<ByteBuffer> getBoundedBuffer() {
		
		return boundedBuffer;
	}
	
	@Override
	public void simulationCommand(final String aCommand) {

		final IPCMechanism mechanism = getIPCMechanism();
		System.out.println("IPC Mechanism: " + mechanism.toString());

		// IPC Mechanism Change
		ProposedStateSet.newCase(this, super.CLIENT_NAME, super.aProposalNumber, mechanism);
		try {

			server.broadcastIPCMechanism(mechanism, this, aProposalNumber, broadcastIPCMechanism);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (!mechanism.toString().equals("NIO")) {
			System.out.println("IPC Mechanism is GIPC or RMI");
			super.simulationCommand(aCommand);
			return;
		}

		commandProcessor.removePropertyChangeListener(clientOutCoupler);
		
		final ByteBuffer bufferCommand = ByteBuffer.wrap(aCommand.getBytes());
		RemoteProposeRequestSent.newCase(this, CLIENT_NAME, aProposalNumber, aCommand);
		nioManager.write(socketChannel, bufferCommand, this);

		
		
		
		commandProcessor.setInputString(aCommand); // all commands go to the first command window
		
		commandProcessor.addPropertyChangeListener(clientOutCoupler);
		aProposalNumber = 1 + aProposalNumber;
	}
	
	@Override
	public HalloweenCommandProcessor getCommandProcessor() {
		return commandProcessor;
	}

	
	@Override
	public void connected(final SocketChannel aSocketChannel) {
		System.out.println("finally worked");
		nioManager.addReadListener(aSocketChannel, this);
		
		
	}

	@Override
	public void notConnected(final SocketChannel argumentone, final Exception argumenttwo) {
		System.out.println("this is stupid, dont want to do this");
		
	}

	@Override
	public void written(final SocketChannel arg0, final ByteBuffer argumentone, final int argumenttwo) {
		System.out.println("placeholder....for credit");
		
	}

	@Override
	public void socketChannelAccepted(final ServerSocketChannel argumentone, final SocketChannel argumenttwo) {
		System.out.println("lalalallalalalalalaa");
		
	}
	
//	@Override
//  public void experimentInput() {
//      long start = System.currentTimeMillis();
//      PerformanceExperimentStarted.newCase(this, start, commands);
//      boolean oldTrace = isTrace();
//      this.trace(false);
//      for (int i = 0; i < commands; i++) {
//          this.simulationCommand(tryCommand);
//      }
//      this.trace(oldTrace);
//      long end = System.currentTimeMillis();
//      PerformanceExperimentEnded.newCase(this, start, end, end - start, commands);
//      
//      
//      System.out.println("Printing out the time:");
//      System.out.println(end - start);
//  }
	
	@Override
	protected void setTracing() {

		FactoryTraceUtility.setTracing();
		BeanTraceUtility.setTracing();
		RMITraceUtility.setTracing();
		ConsensusTraceUtility.setTracing();
		ThreadDelayed.enablePrint();
		GIPCRPCTraceUtility.setTracing();
		NIOTraceUtility.setTracing();

	}
}
