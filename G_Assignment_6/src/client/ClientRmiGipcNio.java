package client;

import java.io.IOException;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.rmi.RemoteException;
import java.util.concurrent.ArrayBlockingQueue;

import assignments.util.MiscAssignmentUtils;
import assignments.util.mainArgs.ClientArgsProcessor;
import assignments.util.mainArgs.ServerArgsProcessor;
import inputport.nio.manager.NIOManager;
import inputport.nio.manager.NIOManagerFactory;
import inputport.nio.manager.factories.classes.AConnectCommandFactory;
import inputport.nio.manager.factories.classes.AnAcceptCommandFactory;
import inputport.nio.manager.factories.selectors.AcceptCommandFactorySelector;
import inputport.nio.manager.factories.selectors.ConnectCommandFactorySelector;
import nio.ClientReadExample;
import stringProcessors.HalloweenCommandProcessor;
import util.annotations.Tags;
import util.interactiveMethodInvocation.IPCMechanism;
import util.tags.DistributedTags;
import util.trace.port.consensus.ProposalLearnedNotificationReceived;
import util.trace.port.consensus.ProposalMade;
import util.trace.port.consensus.ProposedStateSet;
import util.trace.port.consensus.RemoteProposeRequestReceived;
import util.trace.port.consensus.RemoteProposeRequestSent;
import util.trace.port.consensus.communication.CommunicationStateNames;
import utilRead.ClientReadThread;
import utilRead.ReadThreadInterface;

@Tags({ DistributedTags.CLIENT_REMOTE_OBJECT, DistributedTags.RMI, DistributedTags.GIPC, DistributedTags.NIO })
public class ClientRmiGipcNio extends ClientRmiGipc implements ClientInterfaceNio{
	protected NIOManager nioManager = NIOManagerFactory.getSingleton();
	int aServerPort;
	protected SocketChannel socketChannel;
	protected boolean broadcastIPCMechanism = false;
	
	ArrayBlockingQueue<ByteBuffer> boundedBuffer = new ArrayBlockingQueue<ByteBuffer>(500);
	ReadThreadInterface reader = null;
	Thread readThread = null;
	
	@Override
	public void init(String[] args) {
		setTracing();
		setFactories();
		
		aServerPort = ClientArgsProcessor.getNIOServerPort(args);
		System.out.println("NIO SERVER PORT: "+aServerPort);
		
		try {
			socketChannel = SocketChannel.open();
			InetAddress aServerAddress = InetAddress.getByName("localhost");
			
			nioManager.connect(socketChannel, aServerAddress, aServerPort, 
					//0, // do not allow any incoming messages
					SelectionKey.OP_READ,
					this);
		} catch (IOException e) {
			e.printStackTrace();
		}
		

		reader = new ClientReadThread(this);
	
		readThread = new Thread(reader);
				

		readThread.start();
		

		super.init(args);
	}
	
	@Override
	public void setFactories() {
		ConnectCommandFactorySelector.setFactory(new AConnectCommandFactory(0));
	}

	@Override
	public void connected(SocketChannel aSocketChannel) {
		// TODO Auto-generated method stub
		nioManager.addReadListener(aSocketChannel, this);
		System.out.println("New Client connected to server!!!");
		
	}

	@Override
	public void notConnected(SocketChannel arg0, Exception arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void written(SocketChannel arg0, ByteBuffer arg1, int arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void socketChannelAccepted(ServerSocketChannel arg0, SocketChannel arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void socketChannelRead(SocketChannel arg0, ByteBuffer aMessage, int arg2) {
		// TODO Auto-generated method stub
		ByteBuffer copy = MiscAssignmentUtils.deepDuplicate(aMessage);
		boundedBuffer.add(copy);
		
		//TODO NEED TO PARSE THE PROPOSAL NUMBER
		String aMessageString = new String(copy.array());
		System.out.println(aMessageString);
		int aProposalNumber = Integer.parseInt( aMessageString.substring(aMessageString.length()-1) );
		aMessageString =  aMessageString.substring(0, aMessageString.length()-1);
		System.out.println("CLIENT COMMAND: "+aMessageString);
		//int aProposalNumber = 0;
		
		//ByteBuffer bufferCommand = ByteBuffer.wrap(aMessageString.getBytes());
		//boundedBuffer.add(bufferCommand);
		
		ProposalLearnedNotificationReceived.newCase(this, CLIENT_NAME, aProposalNumber, aMessageString);
		//RemoteProposeRequestReceived.newCase(this, CLIENT_NAME, aProposalNumber, aMessageString);
		reader.notifyThread();	
		
	}
	
	@Override
	public ArrayBlockingQueue<ByteBuffer> getBoundedBuffer() {
		// TODO Auto-generated method stub
		return boundedBuffer;
	}
	
	@Override
	public void simulationCommand(String aCommand) {
		String originalCommand = aCommand;
		IPCMechanism mechanism = getIPCMechanism();
		System.out.println("IPC Mechanism: " + mechanism.toString());


		aProposalNumber = 1 + aProposalNumber;
		System.out.println("A PROPOSAL NUMBER: "+aProposalNumber);
		
		if (!mechanism.toString().equals("NIO")) {
			System.out.println("IPC Mechanism is GIPC or RMI");
			super.simulationCommand(aCommand);
			return;
		}

		commandProcessor.removePropertyChangeListener(clientOutCoupler);
		clientOutCoupler = new ClientOutCoupler(server, this, CLIENT_NAME, true);
		commandProcessor.addPropertyChangeListener(clientOutCoupler);
		commandProcessor.setInputString(originalCommand); // all commands go to the first command window
		
		aCommand = aCommand.concat(String.valueOf(aProposalNumber));
		System.out.println("COMMAND + PROPOSAL NUMBER:"+aCommand);
		ByteBuffer bufferCommand = ByteBuffer.wrap(aCommand.getBytes());
		//RemoteProposeRequestSent.newCase(this, CLIENT_NAME, aProposalNumber, aCommand);
		ProposalMade.newCase(this, CommunicationStateNames.COMMAND, -1, originalCommand);
		nioManager.write(socketChannel, bufferCommand, this);
	

		
		
		
		//commandProcessor.setInputString(originalCommand); // all commands go to the first command window
		
		//commandProcessor.addPropertyChangeListener(clientOutCoupler);
		
	}
	
	@Override
	public HalloweenCommandProcessor getCommandProcessor() {
		return commandProcessor;
	}

//	@Override
//	public void argumentProcessor(String aCommand) {
//		// TODO Auto-generated method stub
//		
//	}

	@Override
	public void nioInit(String[] args) {
		
	}

	@Override
	public void simulateCommand(String aCommand) {
		
		
	}
		


}
