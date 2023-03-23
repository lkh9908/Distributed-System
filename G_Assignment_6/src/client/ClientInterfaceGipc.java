package client;

import java.rmi.Remote;
import java.rmi.RemoteException;

import stringProcessors.HalloweenCommandProcessor;
import util.annotations.Tags;
import util.interactiveMethodInvocation.IPCMechanism;
import util.tags.DistributedTags;

import inputport.nio.manager.listeners.SocketChannelConnectListener;
import inputport.nio.manager.listeners.SocketChannelWriteListener;

@Tags({DistributedTags.CLIENT_REMOTE_INTERFACE, DistributedTags.GIPC})
public interface ClientInterfaceGipc  extends Remote{

	void broadcastMetaState(boolean broadcast) throws RemoteException;

	void makeChangeIpcm(IPCMechanism mechanism, int proposalNumber) throws RemoteException;

	void quit(int i) throws RemoteException;
	

	void argumentProcessor(String[] args) throws RemoteException;

	HalloweenCommandProcessor createSimulation(String aPrefix) throws RemoteException;



	void inCoupler(String aNewCommand, int aProposalNumber) throws RemoteException;

	void start(String[] args) throws RemoteException;
}
