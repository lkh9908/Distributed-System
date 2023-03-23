package comp533.server;

import assignments.util.mainArgs.ServerArgsProcessor;
import comp533.client.GIPCClient;
import comp533.client.RMIAndGIPCClient;
import comp533.client.RMIClientInterface;
//import coupledsims.client.Ov;
import inputport.ConnectionListener;
import inputport.InputPort;
import inputport.rpc.GIPCLocateRegistry;
import inputport.rpc.GIPCRegistry;
import port.ATracingConnectionListener;
import stringProcessors.HalloweenCommandProcessor;
import util.annotations.Tags;
import util.interactiveMethodInvocation.SimulationParametersControllerFactory;
import util.tags.DistributedTags;
import util.trace.bean.BeanTraceUtility;
import util.trace.factories.FactoryTraceUtility;
import util.trace.misc.ThreadDelayed;
import util.trace.port.consensus.ConsensusTraceUtility;
import util.trace.port.consensus.ProposalLearnedNotificationSent;
import util.trace.port.consensus.RemoteProposeRequestReceived;
import util.trace.port.consensus.communication.CommunicationStateNames;
import util.trace.port.nio.NIOTraceUtility;
import util.trace.port.rpc.gipc.GIPCObjectLookedUp;
import util.trace.port.rpc.gipc.GIPCObjectRegistered;
import util.trace.port.rpc.gipc.GIPCRPCTraceUtility;
import util.trace.port.rpc.gipc.GIPCRegistryCreated;
import util.trace.port.rpc.rmi.RMITraceUtility;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
@Tags({DistributedTags.SERVER, DistributedTags.RMI, DistributedTags.GIPC})
public class RMIAndGIPCServer extends RMIServer implements GIPCServer{

	private static ServerInterface thisServer = new RMIAndGIPCServer();
    private List<GIPCClient> clients;
    private GIPCRegistry registry;

    public RMIAndGIPCServer() {
        this.clients = new ArrayList<>();
    }

    public static ServerInterface getSingleton() {
        return thisServer;
    }

    @Override
    public void start(String[] arguments) {
        this.init(arguments);
        SimulationParametersControllerFactory.getSingleton().addSimulationParameterListener(this);
        SimulationParametersControllerFactory.getSingleton().processCommands();
    }

    public static void main(String[] arguments) {
        ServerInterface thisServer = getSingleton();
        thisServer.start(arguments);
    }
    
    protected void init(final String[] arguments) {
        this.setTracing();
        super.init(arguments);
        final int gipcServerPort = ServerArgsProcessor.getGIPCServerPort(arguments);
        this.registry = GIPCLocateRegistry.createRegistry(gipcServerPort);
        GIPCRegistryCreated.newCase(this, gipcServerPort);
        this.registry.rebind(GIPCServer.class.getName(), this);
        GIPCObjectRegistered.newCase(this, RMIServerInterface.class.getName(), this, this.registry);
        final InputPort port = this.registry.getInputPort();
        final ConnectionListener listener = new ATracingConnectionListener(port);
        port.addConnectionListener(listener);
    }
    
    @Override
    public void setTracing() {
        FactoryTraceUtility.setTracing();
        BeanTraceUtility.setTracing();
        RMITraceUtility.setTracing();
        ConsensusTraceUtility.setTracing();
        ThreadDelayed.enablePrint();
        GIPCRPCTraceUtility.setTracing();
        NIOTraceUtility.setTracing();
        super.trace(true);
    }

    


    protected void receiveRequestViaGIPC(final String newCommand, final GIPCClient thisClient) {
        try {
            RemoteProposeRequestReceived.newCase(this, CommunicationStateNames.COMMAND, -1, newCommand);
        	System.out.println("@@@??????????????????????");
        	System.out.println(this.clients);
        	System.out.println("@@@!!!!!!!!!!!!!!!!!!!!!!");
            for (GIPCClient otherClientProxy : this.clients) {
            	
            	System.out.println("?????????????????????????");
            	System.out.println(otherClientProxy);
            	System.out.println(thisClient);
            	System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!");
                if (!otherClientProxy.equals(thisClient)) {
                    ProposalLearnedNotificationSent.newCase(this, CommunicationStateNames.BROADCAST_MODE, -1, newCommand);
                    otherClientProxy.NotificationReceiver(CommunicationStateNames.COMMAND, newCommand);
                }
            }
        } catch (RemoteException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void receiveCommandViaGIPC(String newCommand, GIPCClient thisClient) {
        this.receiveRequestViaGIPC(newCommand, thisClient);
    }

    @Override
    public void registerGIPCClients() {
    	System.out.println("register called");
    	//final RMIClientInterface clientProxy = (RMIClientInterface) this.rmiRegistry.lookup(RMIClientInterface.class.getName());
        final GIPCClient clientProxy = (GIPCClient) this.registry.lookup(RMIAndGIPCClient.class, RMIAndGIPCClient.class.getName());
        GIPCObjectLookedUp.newCase(this, clientProxy, RMIAndGIPCClient.class, RMIAndGIPCClient.class.getName(), this.registry);
        this.clients.add(clientProxy);
//        System.out.println(GIPCClient.class);
//        System.out.println(GIPCClient.class.getName());
        System.out.println(this.clients);
        System.out.println(clientProxy);
        System.out.println(this.registry.getInputPort());
        System.out.println(this.registry.getRPCClientPort());
        System.out.println(this.registry.getRPCServerPort());
    	System.out.println("register finished");

    }


}
