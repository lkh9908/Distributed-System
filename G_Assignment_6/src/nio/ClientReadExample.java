package nio;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

import inputport.nio.manager.NIOManager;
import inputport.nio.manager.NIOManagerFactory;
import utilRead.ReadThreadInterface;

public class ClientReadExample implements ReadThreadInterface{
	final ClientInterface client;
	protected NIOManager nioManager = NIOManagerFactory.getSingleton();
	
	public ClientReadExample (final ClientInterface aClient) {
		client = aClient;
	}

	@Override
	public void socketChannelRead(SocketChannel arg0, ByteBuffer arg1, int arg2) {
		
	}

	@Override
	public void run() {
		while(true) {
			synchronized (this) {
				try {
					wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
					break; 
				}
			}
			
			
			ArrayBlockingQueue<ByteBuffer> boundedBuffer = client.getBoundedBuffer();
			
			
					
			ByteBuffer originalMessage = null;
			try {
				originalMessage = boundedBuffer.take();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			String aMessageString = new String(originalMessage.array());
			
		
		}
		
	}
	
	@Override
	public synchronized void notifyThread() {
		this.notify();
	}

}
