package server.remote;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

import assignments.util.MiscAssignmentUtils;
import assignments.util.mainArgs.ServerArgsProcessor;
import inputport.nio.manager.NIOManager;
import inputport.nio.manager.NIOManagerFactory;
import util.trace.bean.BeanTraceUtility;
import util.trace.factories.FactoryTraceUtility;
import util.trace.misc.ThreadDelayed;
import util.trace.port.PortTraceUtility;
import util.trace.port.consensus.ConsensusTraceUtility;
import util.trace.port.consensus.ProposalLearnedNotificationSent;
import util.trace.port.consensus.RemoteProposeRequestReceived;
import util.trace.port.nio.NIOTraceUtility;
import util.trace.port.nio.SocketChannelBound;
import util.trace.port.rpc.gipc.GIPCRPCTraceUtility;
import util.trace.port.rpc.rmi.RMITraceUtility;
import utilRead.ReadThreadInterface;
import utilRead.ServerReadThread;
import inputport.nio.manager.factories.classes.AnAcceptCommandFactory;
import inputport.nio.manager.factories.selectors.AcceptCommandFactorySelector;
import nio.ServerReadExample;

public class ServerNio extends ServerRmiGipc implements ServerInterfaceNio{
	protected NIOManager nioManager = NIOManagerFactory.getSingleton();
	int aServerPort;
	
	List<SocketChannel> socketList = new ArrayList<SocketChannel>();
	ArrayBlockingQueue<ByteBuffer> boundedBuffer = new ArrayBlockingQueue<ByteBuffer>(500);
	ReadThreadInterface reader = null;
	Thread readThread = null;
	SocketChannel currentSocket = null;
	
	@Override
	protected void init(String[] args) {
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
		reader = new ServerReadThread(this);
				
		//Create new readThread
		readThread = new Thread(reader);
		String name = "Read Thread";
		readThread.setName(name);
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
		
		int aProposalNumber = Integer.parseInt( aMessageString.substring(aMessageString.length()-1) );
		aMessageString =  aMessageString.substring(0, aMessageString.length()-1);
		System.out.println("COMMAND IN SERVER: "+ aMessageString);
		
		System.out.println("PROPOSAL NUMBER: "+aProposalNumber);
		//RemoteProposeRequestReceived.newCase(this, SERVER_NAME, aProposalNumber, aMessageString);
		//ProposalLearnedNotificationSent.newCase(this, SERVER_NAME, aProposalNumber, aMessageString);
		reader.notifyThread();
		
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

}
