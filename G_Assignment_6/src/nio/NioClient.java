package nio;

import java.io.IOException;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Scanner;
import java.util.concurrent.ArrayBlockingQueue;

import assignments.util.MiscAssignmentUtils;
import inputport.nio.manager.NIOManager;
import inputport.nio.manager.NIOManagerFactory;
import util.trace.factories.FactoryTraceUtility;
import util.trace.port.nio.NIOTraceUtility;
import utilRead.ReadThreadInterface;
import inputport.nio.manager.listeners.SocketChannelAcceptListener;
import inputport.nio.manager.listeners.SocketChannelConnectListener;
import inputport.nio.manager.listeners.SocketChannelReadListener;
import inputport.nio.manager.listeners.SocketChannelWriteListener;



public class NioClient implements SocketChannelConnectListener, SocketChannelWriteListener,
SocketChannelAcceptListener, SocketChannelReadListener, ClientInterface{
	protected NIOManager managerNio = NIOManagerFactory.getSingleton();
	protected SocketChannel socketChannel;
	
	protected Scanner scanner = new Scanner(System.in);
	
	ArrayBlockingQueue<ByteBuffer> boundedBuffer = new ArrayBlockingQueue<ByteBuffer>(500);
	ReadThreadInterface reader = null;
	Thread thisReader = null;

	protected NioClient(final int aServerPort) {
		setTracing();
		initialize(aServerPort);
	}
	
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


	@Override
	public void processInput() {
		scanner = new Scanner(System.in);
		while (true) {
			System.out.println("Enter a line of input to be printed remotely");
			final String aNextLine = scanner.nextLine();
			final ByteBuffer aWriteMessage = ByteBuffer.wrap(aNextLine.getBytes());
			managerNio.write(socketChannel, aWriteMessage, this);
		}
	}

	void setTracing() {
		FactoryTraceUtility.setTracing();
		NIOTraceUtility.setTracing();
	}

	
	@Override
	public void socketChannelRead(final SocketChannel newSocketChannel, final ByteBuffer aMessage, final int aLength) {
		final ByteBuffer copy = MiscAssignmentUtils.deepDuplicate(aMessage);
		boundedBuffer.add(copy);
		
		reader.notifyThread();		
	}

	@Override
	public void socketChannelAccepted(final ServerSocketChannel arg0, final SocketChannel arg1) {
				
	}

	@Override
	public ArrayBlockingQueue<ByteBuffer> getBoundedBuffer() {
		return boundedBuffer;
	}

	void initialize(final int aServerPort) {
		try {
			socketChannel = SocketChannel.open();
			final InetAddress aServerAddress = InetAddress.getByName("localhost");
			
			managerNio.connect(socketChannel, aServerAddress, aServerPort, 
					SelectionKey.OP_READ,
					this);
		} catch (IOException exception) {
			exception.printStackTrace();
		}
		
		reader = new ClientReadExample(this);			
		thisReader = new Thread(reader);	
		thisReader.start();
	}

	@Override
	public void connected(final SocketChannel aSocketChannel) {
		managerNio.addReadListener(aSocketChannel, this);
		System.out.println("connected?????????????????????????????");
	}

	@Override
	public void notConnected(final SocketChannel theSocketChannel, final Exception exception) {

	}

	@Override
	public void written(SocketChannel newSocketChannel, final ByteBuffer theWriteBuffer, final int sendId) {
		System.out.println("step reached.........................");
	}

}