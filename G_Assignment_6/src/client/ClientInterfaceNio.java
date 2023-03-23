package client;

import java.nio.ByteBuffer;
import java.util.concurrent.ArrayBlockingQueue;

import inputport.nio.manager.listeners.SocketChannelAcceptListener;
import inputport.nio.manager.listeners.SocketChannelConnectListener;
import inputport.nio.manager.listeners.SocketChannelReadListener;
import inputport.nio.manager.listeners.SocketChannelWriteListener;
import stringProcessors.HalloweenCommandProcessor;

public interface ClientInterfaceNio extends SocketChannelConnectListener, SocketChannelWriteListener,
SocketChannelAcceptListener, SocketChannelReadListener{


	HalloweenCommandProcessor getCommandProcessor();
	void simulateCommand(String aCommand);
	void nioInit(String[] args);

	void setFactories();
	ArrayBlockingQueue<ByteBuffer> getBoundedBuffer();

	
	
}
