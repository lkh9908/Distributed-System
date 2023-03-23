package nio;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

import inputport.nio.manager.listeners.SocketChannelAcceptListener;
import inputport.nio.manager.listeners.SocketChannelReadListener;
import inputport.nio.manager.listeners.SocketChannelWriteListener;

public interface NIOManagerPrintServer extends SocketChannelAcceptListener, SocketChannelReadListener, SocketChannelWriteListener {
	SocketChannel getSocketChannel();
	
	ArrayBlockingQueue<ByteBuffer> getBoundedBuffer();

	List<SocketChannel> getSocketList();



}