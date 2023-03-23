package utilRead;

import util.annotations.Tags;
import util.tags.DistributedTags;
import inputport.nio.manager.listeners.SocketChannelReadListener;

@Tags({DistributedTags.NIO, DistributedTags.READ_THREAD_INTERFACE})
public interface ReadThreadInterface extends SocketChannelReadListener, Runnable{

	void notifyThread();


}
