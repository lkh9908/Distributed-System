package server.remote;


import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

import inputport.nio.manager.listeners.SocketChannelAcceptListener;
import inputport.nio.manager.listeners.SocketChannelReadListener;
import inputport.nio.manager.listeners.SocketChannelWriteListener;

public interface ServerInterfaceNio extends SocketChannelAcceptListener, SocketChannelReadListener, SocketChannelWriteListener {

	ArrayBlockingQueue<ByteBuffer> getBoundedBuffer();

	List<SocketChannel> getSocketList();

	SocketChannel getSocketChannel();

	void setFactories();

}