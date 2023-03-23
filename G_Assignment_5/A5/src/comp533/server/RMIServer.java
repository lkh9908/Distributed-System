package comp533.server;

import assignments.util.inputParameters.AnAbstractSimulationParametersBean;
import assignments.util.mainArgs.ServerArgsProcessor;
import comp533.client.RMIClientInterface;
import util.annotations.Tags;
import util.interactiveMethodInvocation.IPCMechanism;
import util.interactiveMethodInvocation.SimulationParametersControllerFactory;
import util.tags.DistributedTags;
import util.trace.factories.FactoryTraceUtility;
import util.trace.misc.ThreadDelayed;
import util.trace.port.consensus.ConsensusTraceUtility;
import util.trace.port.consensus.ProposalLearnedNotificationSent;
import util.trace.port.consensus.RemoteProposeRequestReceived;
import util.trace.port.consensus.communication.CommunicationStateNames;
import util.trace.port.nio.NIOTraceUtility;
import util.trace.port.rpc.rmi.RMIObjectLookedUp;
import util.trace.port.rpc.rmi.RMIObjectRegistered;
import util.trace.port.rpc.rmi.RMIRegistryLocated;
import util.trace.port.rpc.rmi.RMITraceUtility;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
@Tags({DistributedTags.SERVER, DistributedTags.RMI})
public class RMIServer extends AnAbstractSimulationParametersBean implements ServerInterface, RMIServerInterface {

	private static ServerInterface thisServer = new RMIServer();
    private List<RMIClientInterface> registeredRMIClients;
    private Registry rmiRegistry;

    RMIServer() {
        this.registeredRMIClients = new ArrayList<>();
    }

    public static ServerInterface getSingleton() {
        return thisServer;
    }

    @Override
    public void quit(final int aCode) {
        System.exit(aCode);
    }

    @Override
    public void setTracing() {
        FactoryTraceUtility.setTracing();
        RMITraceUtility.setTracing();
        ConsensusTraceUtility.setTracing();
        NIOTraceUtility.setTracing();
        ThreadDelayed.enablePrint();
        this.trace(true);
    }
    

    @Override
    public void start(final String[] arguments) {
        this.init(arguments);
        SimulationParametersControllerFactory.getSingleton().addSimulationParameterListener(this);
        SimulationParametersControllerFactory.getSingleton().processCommands();
    }

    public static void main(final String[] arguments) {
        final ServerInterface thisServer = getSingleton();
        thisServer.start(arguments);
    }

    @Override
    public void locateRegistry(final int port, String host) throws RemoteException {
        this.rmiRegistry = LocateRegistry.getRegistry(port);
        RMIRegistryLocated.newCase(this, host, port, this.rmiRegistry);
    }

    @Override
    public void exportServerProxy(final int rmiServerPort) throws RemoteException {
        UnicastRemoteObject.exportObject(this, rmiServerPort);
        this.rmiRegistry.rebind(RMIServerInterface.class.getName(), this);
        RMIObjectRegistered.newCase(this, RMIServerInterface.class.getName(), this, this.rmiRegistry);
    }

    @Override
    public void registerRMIClients() {
        try {
            final RMIClientInterface clientProxy = (RMIClientInterface) this.rmiRegistry.lookup(RMIClientInterface.class.getName());
            RMIObjectLookedUp.newCase(this, clientProxy, RMIClientInterface.class.getName(), this.rmiRegistry);
            this.registeredRMIClients.add(clientProxy);
        } catch (RemoteException | NotBoundException ex) {
            ex.printStackTrace();
        }
    }

    protected void receiveRequestViaRMI(final String anObjectName, final Object aProposal, final RMIClientInterface currentClientProxy) {
        try {
            RemoteProposeRequestReceived.newCase(this, anObjectName, -1, aProposal);
            for (RMIClientInterface otherClientProxy : this.registeredRMIClients) {
                if (!otherClientProxy.equals(currentClientProxy)) {
                    ProposalLearnedNotificationSent.newCase(this, CommunicationStateNames.BROADCAST_MODE, -1, anObjectName);
                    otherClientProxy.receiveNotificationsViaRMI(anObjectName, aProposal);
                }
            }
        } catch (RemoteException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void receiveCommandViaRMI(final String aCommand, final RMIClientInterface clientProxy) {
        this.receiveRequestViaRMI(CommunicationStateNames.COMMAND, aCommand, clientProxy);
    }

    @Override
    public void receiveIPCMechanismViaRMI(final IPCMechanism ipcMechanism, final RMIClientInterface clientProxy) {
        this.receiveRequestViaRMI(CommunicationStateNames.IPC_MECHANISM, ipcMechanism, clientProxy);
    }

    protected void init(String[] arguments) {
        try {
            this.setTracing();
            final int rmiRegistryPort = ServerArgsProcessor.getRegistryPort(arguments), rmiServerPort = ServerArgsProcessor.getServerPort(arguments);
            final String host = ServerArgsProcessor.getRegistryHost(arguments);
            this.locateRegistry(rmiRegistryPort, host);
            this.exportServerProxy(rmiServerPort);
        } catch (RemoteException ex) {
            ex.printStackTrace();
        }
    }

}
