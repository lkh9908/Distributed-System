package nio;

import java.nio.ByteBuffer;
import java.util.concurrent.ArrayBlockingQueue;

public interface ClientInterface {


	ArrayBlockingQueue<ByteBuffer> getBoundedBuffer();
	
	void processInput();


}
