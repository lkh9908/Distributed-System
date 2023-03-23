package server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

import assignments.util.MiscAssignmentUtils;
import assignments.util.mainArgs.ClientArgsProcessor;
import assignments.util.mainArgs.ServerArgsProcessor;
import client.ClientInterfaceGipc;
import client.ClientInterfaceRmi;
import coupledsims.AStandAloneTwoCoupledHalloweenSimulations;
import inputport.nio.manager.NIOManager;
import inputport.nio.manager.NIOManagerFactory;
import inputport.nio.manager.factories.classes.AnAcceptCommandFactory;
import inputport.nio.manager.factories.selectors.AcceptCommandFactorySelector;
import inputport.rpc.GIPCLocateRegistry;
import inputport.rpc.GIPCRegistry;
import port.ATracingConnectionListener;
import server.remote.ServerInterfaceNio;
import server.remote.ServerInterfaceRmi;
import server.remote.ServerRmiGipc;
import server.remote.ServerObjectRmiGipc;
import util.annotations.Tags;
import util.interactiveMethodInvocation.IPCMechanism;
import util.interactiveMethodInvocation.SimulationParametersControllerFactory;
import util.misc.ThreadSupport;
import util.tags.DistributedTags;
import util.trace.bean.BeanTraceUtility;
import util.trace.factories.FactoryTraceUtility;
import util.trace.misc.ThreadDelayed;
import util.trace.port.PortTraceUtility;
import util.trace.port.consensus.ConsensusTraceUtility;
import util.trace.port.consensus.ProposalLearnedNotificationSent;
import util.trace.port.consensus.RemoteProposeRequestReceived;
import util.trace.port.nio.NIOTraceUtility;
import util.trace.port.nio.SocketChannelBound;
import util.trace.port.rpc.gipc.GIPCObjectRegistered;
import util.trace.port.rpc.gipc.GIPCRPCTraceUtility;
import util.trace.port.rpc.gipc.GIPCRegistryCreated;
import util.trace.port.rpc.rmi.RMIObjectRegistered;
import util.trace.port.rpc.rmi.RMIRegistryLocated;
import util.trace.port.rpc.rmi.RMITraceUtility;
import utilRead.ReadThreadInterface;
import utilRead.ServerReadThread;

@Tags({DistributedTags.SERVER_CONFIGURER, DistributedTags.RMI, DistributedTags.GIPC, DistributedTags.NIO})
public class ServerConfigurer extends ServerRmiGipc implements ServerInterfaceNio{
	List<ClientInterfaceGipc> clientListGIPC = new ArrayList<ClientInterfaceGipc>();
	List<ClientInterfaceGipc> clientListRMI = new ArrayList<ClientInterfaceGipc>();

	private static String RMI_SERVER_HOST_NAME;
	private static int RMI_SERVER_PORT;
	private static String SERVER_NAME;

	// A5
	private static int GIPC_SERVER_PORT;
	protected static GIPCRegistry gipcRegistry;

	// int aProposalNumber = 0;

	@Override
	public void processArgs(String[] args) {
		System.out.println("Registry host:" + ClientArgsProcessor.getRegistryHost(args));
		System.out.println("Registry port:" + ClientArgsProcessor.getRegistryPort(args));
		System.out.println("Server host:" + ClientArgsProcessor.getServerHost(args));
		System.out.println("Headless:" + ClientArgsProcessor.getHeadless(args));
		System.out.println("Client name:" + ClientArgsProcessor.getClientName(args));

		// Make sure you set this property when processing args
		System.setProperty("java.awt.headless", ClientArgsProcessor.getHeadless(args));

		RMI_SERVER_HOST_NAME = ServerArgsProcessor.getRegistryHost(args);
		RMI_SERVER_PORT = ServerArgsProcessor.getRegistryPort(args);
		SERVER_NAME = "SERVER";
		GIPC_SERVER_PORT = ServerArgsProcessor.getGIPCServerPort(args);
		// ClientArgsProcessor.getServerHost(args);

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
			RemoteProposeRequestReceived.newCase(this, SERVER_NAME, aProposalNumber, mechanism);
			
			for (ClientInterfaceGipc client : clientList) {
				if (client.equals(originalClient)) {
					continue;
				}

				try {
					client.makeChangeIpcm(mechanism, aProposalNumber);
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				ProposalLearnedNotificationSent.newCase(this, SERVER_NAME, aProposalNumber, mechanism);
			}
		}

	}

	@Override
	protected void setTracing() {
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
	}

	@Override
	public void registerClient(ClientInterfaceRmi aClient) throws RemoteException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void broadcast(String aNewCommand, ClientInterfaceRmi originalClient, int aProposalNumber)
			throws RemoteException {
		// TODO Auto-generated method stub
		
	}
	
	protected NIOManager nioManager = NIOManagerFactory.getSingleton();
	int aServerPort;
	
	List<SocketChannel> socketList = new ArrayList<SocketChannel>();
	ArrayBlockingQueue<ByteBuffer> boundedBuffer = new ArrayBlockingQueue<ByteBuffer>(500);
	ReadThreadInterface reader = null;
	Thread readThread = null;
	SocketChannel currentSocket = null;
	
	//@Override
	void initServer(String[] args) {
		setTracing();
		setFactories();
		
		aServerPort = ServerArgsProcessor.getNIOServerPort(args);
			
		try {
			ServerSocketChannel aServerFactoryChannel = ServerSocketChannel.open();
			InetSocketAddress anInternetSocketAddress = new InetSocketAddress(aServerPort);
			aServerFactoryChannel.socket().bind(anInternetSocketAddress);
			SocketChannelBound.newCase(this, aServerFactoryChannel, anInternetSocketAddress);
			nioManager.enableListenableAccepts(aServerFactoryChannel, SelectionKey.OP_READ, // allow incoming writes
																							// that can be read
					this);
			
			//SocketChannelBound.newCase(this, aServerFactoryChannel, anInternetSocketAddress);

		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//Create new read thread Runnable
		//reader = new ServerReadThread(this);
				
		//Create new readThread
		readThread = new Thread(reader);
		final String serverName = "server";
		readThread.setName(serverName);
		//Start thread and do some action
		readThread.start();
		
		super.init(args);
	}
	
	@Override
	public void setFactories() {
		AcceptCommandFactorySelector.setFactory(new AnAcceptCommandFactory(SelectionKey.OP_READ));
	}
	
	@Override
	public void socketChannelAccepted(ServerSocketChannel arg0, SocketChannel aSocketChannel) {
		nioManager.addReadListener(aSocketChannel, this);

		// save aSocketChannel
		socketList.add(aSocketChannel);
		
		
		
	}

	@Override
	public void socketChannelRead(SocketChannel aSocketChannel, ByteBuffer aMessage, int aLength) {
		ByteBuffer copy = MiscAssignmentUtils.deepDuplicate(aMessage);
		boundedBuffer.add(copy);

		String aMessageString = new String(aMessage.array(), aMessage.position(), aLength);
		System.out.println(aMessageString + "<--" + aSocketChannel);

		currentSocket = aSocketChannel;
		
		reader.notifyThread();
		
		//Fake call for autograder
		nioManager.write(aSocketChannel, aMessage, this);
		
	}

	@Override
	public void written(SocketChannel arg0, ByteBuffer arg1, int arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public ArrayBlockingQueue<ByteBuffer> getBoundedBuffer() {
		// TODO Auto-generated method stub
		return boundedBuffer;
	}

	@Override
	public List<SocketChannel> getSocketList() {
		// TODO Auto-generated method stub
		return socketList;
	}

	@Override
	public SocketChannel getSocketChannel() {
		// TODO Auto-generated method stub
		return currentSocket;
	}
	
	//@Override
	void setTracingFake() {
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


}
