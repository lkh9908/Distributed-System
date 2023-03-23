package server;

import java.rmi.RemoteException;

import server.remote.ServerInterfaceRmi;
import server.remote.ServerRmiGipc;
import server.remote.ServerNio;
import server.remote.ServerObjectRmiGipc;

import util.annotations.Tags;
import util.tags.DistributedTags;

@Tags({ DistributedTags.SERVER, DistributedTags.RMI, DistributedTags.GIPC, DistributedTags.NIO })
public class Server {
	
	
	public static void main(final String[] args) {
		
		//GIPC Server is a subclass of RMI server, so we can just call super.method() on gipc
		//final ServerRemoteInterfaceRMI server = new ServerRemoteObjectGIPC();
		final ServerInterfaceRmi server = new ServerNio();
		
		try {
			server.start(args);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	


}
