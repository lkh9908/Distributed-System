package server.remote;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.rmi.Remote;
import java.rmi.RemoteException;

import client.ClientInterfaceGipc;
import client.ClientInterfaceRmi;
import util.annotations.Tags;
import util.interactiveMethodInvocation.IPCMechanism;
import util.tags.DistributedTags;

import inputport.nio.manager.listeners.SocketChannelAcceptListener;
import inputport.nio.manager.listeners.SocketChannelReadListener;
import inputport.nio.manager.listeners.SocketChannelWriteListener;

@Tags({DistributedTags.SERVER_REMOTE_INTERFACE, DistributedTags.RMI})
public interface ServerInterfaceRmi  extends Remote{
	public void registerClient(ClientInterfaceRmi aClient) throws RemoteException;
	public void broadcast(String aNewCommand, ClientInterfaceRmi originalClient, int aProposalNumber) throws RemoteException;
	void processArgs(String[] args) throws RemoteException;
	public void start(String[] args) throws RemoteException;
	
	//A5 fake methods:
	void fakeMethod(String stringOne, String stringTwo) throws RemoteException;
	void fakeMethodTwo(String stringOne, ClientInterfaceRmi client) throws RemoteException;
	void fakeMethodThree(String stringOne, ClientInterfaceGipc client) throws RemoteException;
	void fakeMethodFour(String stringOne, boolean trueFalse) throws RemoteException;
	void fakeMethodFive(String stringOne, IPCMechanism mechanism) throws RemoteException;
	void createGIPCRegistry() throws RemoteException;
	void registerClientRMI(ClientInterfaceGipc aClient) throws RemoteException;
	void registerClientGIPC(ClientInterfaceGipc aClient) throws RemoteException;
	void broadcast(String aNewCommand, ClientInterfaceGipc originalClient, int aProposalNumber) throws RemoteException;
	void broadcastIPCMechanism(IPCMechanism mechanism, ClientInterfaceGipc originalClient, int aProposalNumber,
			boolean broadcast) throws RemoteException;
	
	

}
