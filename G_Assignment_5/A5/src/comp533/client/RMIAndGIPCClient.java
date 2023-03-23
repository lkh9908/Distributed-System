package comp533.client;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import assignments.util.mainArgs.ClientArgsProcessor;
import comp533.server.GIPCServer;
import inputport.rpc.GIPCLocateRegistry;
import inputport.rpc.GIPCRegistry;
import util.annotations.Tags;
import util.interactiveMethodInvocation.IPCMechanism;
import util.interactiveMethodInvocation.SimulationParametersControllerFactory;
import util.tags.DistributedTags;
import util.trace.bean.BeanTraceUtility;
import util.trace.port.PerformanceExperimentEnded;
import util.trace.port.PerformanceExperimentStarted;
import util.trace.port.consensus.RemoteProposeRequestSent;
import util.trace.port.consensus.communication.CommunicationStateNames;
import util.trace.port.rpc.gipc.GIPCObjectLookedUp;
import util.trace.port.rpc.gipc.GIPCObjectRegistered;
import util.trace.port.rpc.gipc.GIPCRPCTraceUtility;
import util.trace.port.rpc.gipc.GIPCRegistryLocated;

@SuppressWarnings("serial")
@Tags({DistributedTags.CLIENT, DistributedTags.RMI, DistributedTags.GIPC})
public class RMIAndGIPCClient extends RMIClient implements GIPCClient{

	private static int number = 0;
    private GIPCRegistry registry;
    protected GIPCServer proxyServer;
    
    public static final String tryCommand = "move 1 -1";
    protected int commands = 500;
    
    private static ClientInterface thisClient = new RMIAndGIPCClient();
    
    @Override
    public void start(String[] arguments) {
        this.init(arguments);
        SimulationParametersControllerFactory.getSingleton().addSimulationParameterListener(this);
        SimulationParametersControllerFactory.getSingleton().processCommands();
    }

    public static void main(String[] arguments) {
        ClientInterface thisClient = new RMIAndGIPCClient();
        thisClient.start(arguments);
    }
    

    @Override
    public void setTracing() {
        super.setTracing();
        BeanTraceUtility.setTracing();
        GIPCRPCTraceUtility.setTracing();
    }

    public RMIAndGIPCClient() {
        number++;
    }
    public static ClientInterface getSingleton() {
        return thisClient;
    }
    @Override
    protected void init(final String[] arguments) {
        this.setTracing();
        super.init(arguments);
        
        String hostName = ClientArgsProcessor.getServerHost(arguments);
        final int port = ClientArgsProcessor.getGIPCPort(arguments);
        
        this.locateGIPCRegistry(port, hostName);
        this.lookupGIPCServerProxy();

		try {
			this.exportGIPCClientProxy();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        System.out.println("should be registered");
        System.out.println(this.proxyServer);
//        System.out.println(thisClient.getClass());
//        System.out.println(thisClient.getClass().getName());
        System.out.println(this);
        this.proxyServer.registerGIPCClients();
    }
    
    protected void exportGIPCClientProxy() throws RemoteException {
//    	UnicastRemoteObject.exportObject(this, 0);
        this.registry.rebind(RMIAndGIPCClient.class.getName(), this);
        GIPCObjectRegistered.newCase(this, RMIAndGIPCClient.class.getName(), this, this.registry);

    }


    @Override
    public void NotificationReceiver(final String newCommand, final Object newProposal) {
        this.receiveNotifications(CommunicationStateNames.COMMAND, newCommand);
    }

    @Override
    public void experimentInput() {
        long start = System.currentTimeMillis();
        PerformanceExperimentStarted.newCase(this, start, commands);
        boolean oldTrace = isTrace();
        this.trace(false);
        for (int i = 0; i < commands; i++) {
            this.simulationCommand(tryCommand);
        }
        this.trace(oldTrace);
        long end = System.currentTimeMillis();
        PerformanceExperimentEnded.newCase(this, start, end, end - start, commands);
        
        
        System.out.println("Printing out the time:");
        System.out.println(end - start);
    }


    protected void sendCommand(final String newCommand) {
        this.thisProcessor.setInputString(newCommand);
        RemoteProposeRequestSent.newCase(this, CommunicationStateNames.IPC_MECHANISM, -1, ipcMechanism);
        this.proxyServer.receiveCommandViaGIPC(newCommand, this);
    }

    @Override
    public void simulationCommand(final String newCommand) {
        if (this.ipcMechanism.equals(IPCMechanism.RMI)) {
            super.simulationCommand(newCommand);
        } else {
            this.sendCommand(newCommand);
        }
    }

    protected void locateGIPCRegistry(final int port, final String hostName) {
        this.registry = GIPCLocateRegistry.getRegistry(hostName, port, GIPCClient.CLIENT_NAME + number);
        GIPCRegistryLocated.newCase(this, hostName,port, GIPCClient.class.getName());
    }

    protected void lookupGIPCServerProxy() {
    	System.out.println("I am not here");
    	System.out.println(GIPCServer.class);
    	System.out.println(GIPCServer.class.getName());
    	
        this.proxyServer = (GIPCServer) registry.lookup(GIPCServer.class, GIPCServer.class.getName());
        System.out.println("I am here");
        GIPCObjectLookedUp.newCase(this, this.proxyServer, GIPCServer.class, GIPCServer.class.getName(), this.registry);
    }


    
}
