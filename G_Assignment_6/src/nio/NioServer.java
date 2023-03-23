package nio;

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

import client.ClientInterfaceGipc;
import inputport.nio.manager.NIOManager;
import inputport.nio.manager.NIOManagerFactory;
import inputport.nio.manager.factories.classes.AReadingAcceptCommandFactory;
import inputport.nio.manager.factories.classes.AnAcceptCommandFactory;
import inputport.nio.manager.factories.selectors.AcceptCommandFactorySelector;
import inputport.nio.manager.listeners.SocketChannelAcceptListener;
import inputport.nio.manager.listeners.SocketChannelReadListener;
import inputport.nio.manager.listeners.SocketChannelWriteListener;
import inputport.nio.manager.listeners.WriteBoundedBufferListener;
import util.trace.factories.FactoryTraceUtility;
import util.trace.port.nio.NIOTraceUtility;
import util.trace.port.nio.SocketChannelBound;
import utilRead.ReadThreadInterface;

public class NioServer implements NIOManagerPrintServer {

	List<SocketChannel> socketList = new ArrayList<SocketChannel>();
	ArrayBlockingQueue<ByteBuffer> boundedBuffer = new ArrayBlockingQueue<ByteBuffer>(500);
	ReadThreadInterface reader = null;
	Thread readThread = null;
	SocketChannel currentSocket = null;
	
	protected NIOManager nioManager = NIOManagerFactory.getSingleton();
	
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
	public NioServer(final int aServerPort) {
		setTracing();
		setFactories();
		initialize(aServerPort);
	}

	void setFactories() {
		AcceptCommandFactorySelector.setFactory(new AnAcceptCommandFactory(SelectionKey.OP_READ));
	}
	
	void setTracing() {
		FactoryTraceUtility.setTracing();
		NIOTraceUtility.setTracing();
	}

	void initialize(final int aServerPort) {
		try {
			final ServerSocketChannel aServerFactoryChannel = ServerSocketChannel.open();
			final InetSocketAddress anInternetSocketAddress = new InetSocketAddress(aServerPort);
			aServerFactoryChannel.socket().bind(anInternetSocketAddress);
			SocketChannelBound.newCase(this, aServerFactoryChannel, anInternetSocketAddress);
			nioManager.enableListenableAccepts(aServerFactoryChannel, SelectionKey.OP_READ, // allow incoming writes
																							// that can be read
					this);

		} catch (IOException e) {
			e.printStackTrace();
		}
		
		reader = new ServerReadExample(this);
				
		readThread = new Thread(reader);
		
		readThread.start();
	}

	@Override
	public void socketChannelAccepted(ServerSocketChannel aServerSocketChannel, SocketChannel aSocketChannel) {
		nioManager.addReadListener(aSocketChannel, this);

		socketList.add(aSocketChannel);
	}

	@Override
	public void socketChannelRead(final SocketChannel aSocketChannel, final ByteBuffer aMessage, final int aLength) {
		final ByteBuffer copy = MiscAssignmentUtils.deepDuplicate(aMessage);
		boundedBuffer.add(copy);

		final String aMessageString = new String(aMessage.array(), aMessage.position(), aLength);
		System.out.println(aMessageString + "<--" + aSocketChannel);

		currentSocket = aSocketChannel;
		
//		public void findRegistry(int registryPort, String registryHost) throws RemoteException {
//	        this.rmiRegistry = LocateRegistry.getRegistry(registryPort);
//	        RMIRegistryLocated.newCase(this, registryHost, registryPort, this.rmiRegistry);
//	        setBroadcastMetaState(true);
//	        setIPCMechanism(ipcMechanism);
//	    }

//	    @Override
//	    public void exportServer(int serverPort) throws RemoteException {
//	        UnicastRemoteObject.exportObject(this, serverPort);
//	        this.rmiRegistry.rebind(RemoteServerInterface.class.getName(), this);
//	        RMIObjectRegistered.newCase(this, RemoteServerInterface.class.getName(), this, rmiRegistry);
//	    }
		System.out.println("is here wrong%%%%%%%%%%%%%%%%%%%%%%%%%%");
		reader.notifyThread();
		System.out.println("passed)))))))))))))))))))))))))))))))))))");

	}
	
	@Override
	public ArrayBlockingQueue<ByteBuffer> getBoundedBuffer(){
		return boundedBuffer;
		
	}
	
	@Override
	public List<SocketChannel> getSocketList(){
		return socketList;
	}
	


	@Override
	public void written(SocketChannel socket, ByteBuffer aMessage, int aLength) {
		String aMessageString = new String(aMessage.array());
		System.out.println("SERVER SENT MESSAGE TO CLIENT: " + aMessageString + "-->" + socket);
	}

	@Override
	public SocketChannel getSocketChannel() {
		return currentSocket;
	}
}
