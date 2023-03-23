package client;

import java.rmi.Remote;
import java.rmi.RemoteException;

import stringProcessors.HalloweenCommandProcessor;
import util.annotations.Tags;
import util.interactiveMethodInvocation.IPCMechanism;
import util.tags.DistributedTags;

import inputport.nio.manager.listeners.SocketChannelConnectListener;
import inputport.nio.manager.listeners.SocketChannelWriteListener;

@Tags({DistributedTags.CLIENT_REMOTE_INTERFACE, DistributedTags.RMI})
public interface ClientInterfaceRmi  extends Remote{

	//void processArgs(String[] args);
	
	void inCoupler(String aNewCommand, int aProposalNumber) throws RemoteException;

	void start(String[] args) throws RemoteException;

	void processArgs(String[] args) throws RemoteException;

	HalloweenCommandProcessor createSimulation(String aPrefix) throws RemoteException;

	void changeIPCMechanism(IPCMechanism mechanism, int proposalNumber) throws RemoteException;

}
