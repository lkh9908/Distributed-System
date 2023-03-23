package comp533.client;

import assignments.util.inputParameters.AnAbstractSimulationParametersBean;
import assignments.util.mainArgs.ClientArgsProcessor;
import comp533.server.RMIServerInterface;
import coupledsims.ASimulationCoupler;
import main.BeauAndersonFinalProject;
import stringProcessors.HalloweenCommandProcessor;
import util.annotations.Tags;
import util.interactiveMethodInvocation.IPCMechanism;
import util.interactiveMethodInvocation.SimulationParametersControllerFactory;
import util.misc.ThreadSupport;
import util.tags.DistributedTags;
import util.trace.Tracer;
import util.trace.factories.FactoryTraceUtility;
import util.trace.misc.ThreadDelayed;
import util.trace.port.PerformanceExperimentEnded;
import util.trace.port.PerformanceExperimentStarted;
import util.trace.port.PortTraceUtility;
import util.trace.port.consensus.*;
import util.trace.port.consensus.communication.CommunicationStateNames;
import util.trace.port.nio.NIOTraceUtility;
import util.trace.port.rpc.rmi.RMIObjectLookedUp;
import util.trace.port.rpc.rmi.RMIObjectRegistered;
import util.trace.port.rpc.rmi.RMIRegistryLocated;
import util.trace.port.rpc.rmi.RMITraceUtility;

import java.beans.PropertyChangeListener;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

@SuppressWarnings("serial")
@Tags({DistributedTags.CLIENT, DistributedTags.RMI})
public class RMIClient extends AnAbstractSimulationParametersBean implements ClientInterface, RMIClientInterface {

    protected HalloweenCommandProcessor thisProcessor;
    
    public static final String tryCommand = "move 1 -1";
    protected int commands = 500;
    
    protected Registry rmiRegistry;

    private static ClientInterface thisClient = new RMIClient();
    
    protected RMIServerInterface proxyServer;
    
    transient protected PropertyChangeListener coupler;
    static int clients = 0;
    
    final int SIMULATION_Y_OFFSET = 0;
    final int SIMULATION_WIDTH = 400;
    final int SIMULATION_HEIGHT = 765;
    final int SIMULATION_X_OFFSET = 0;

    public static ClientInterface getSingleton() {
        return thisClient;
    }

    protected HalloweenCommandProcessor createSimulation(final String inputString) {
        return 	BeauAndersonFinalProject.createSimulation(
                inputString,
                SIMULATION_X_OFFSET,
                SIMULATION_Y_OFFSET,
                SIMULATION_WIDTH,
                SIMULATION_HEIGHT,
                SIMULATION_X_OFFSET,
                SIMULATION_Y_OFFSET);
    }

    

    
    protected RMIClient() {
        clients += 1;
        this.thisProcessor = createSimulation(clients + ":");
        this.coupler = new ASimulationCoupler(this.thisProcessor);
        this.thisProcessor.addPropertyChangeListener(this.coupler);
    }

    
    public static void main(final String[] arguments) {
        final ClientInterface thisClient = new RMIClient();
        thisClient.start(arguments);
    }

    @Override
    public void setTracing() {
        PortTraceUtility.setTracing();
        FactoryTraceUtility.setTracing();
        RMITraceUtility.setTracing();
        ConsensusTraceUtility.setTracing();
        NIOTraceUtility.setTracing();
        ThreadDelayed.enablePrint();
        this.trace(true);
    }

    protected void init(final String[] arguments) {
        try {
            this.setTracing();
            final int rmiPort = ClientArgsProcessor.getRegistryPort(arguments);
            final String rmiRegistryHost = ClientArgsProcessor.getRegistryHost(arguments);
            this.locateRegistry(rmiPort, rmiRegistryHost);
            this.lookupServerProxy();
            this.exportClientProxy();
            this.proxyServer.registerRMIClients();
        } catch (RemoteException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void start(final String[] arguments) {
        this.init(arguments);
        SimulationParametersControllerFactory.getSingleton().addSimulationParameterListener(this);
        SimulationParametersControllerFactory.getSingleton().processCommands();
    }

    @Override
    public void trace(boolean newValue) {
        super.trace(newValue);
        Tracer.showInfo(isTrace());
    }

    @Override
    public synchronized void setAtomicBroadcast(Boolean newValue) {
        super.setAtomicBroadcast(newValue);
        this.thisProcessor.setConnectedToSimulation(!isAtomicBroadcast());
    }

    @Override
    public void quit(int aCode) {
        System.exit(aCode);
        try {
            this.proxyServer.quit(aCode);
        } catch (RemoteException ex) {
            ex.printStackTrace();
        }
    }

    
    protected void sendProposalViaRMI(final Object newProposal, final String stateName) {
        try {
            RemoteProposeRequestSent.newCase(this, stateName, -1, newProposal);
            if (stateName.equals(CommunicationStateNames.COMMAND)) {
                final String newCommand = (String) newProposal;
                this.thisProcessor.setInputString(newCommand);
                this.proxyServer.receiveCommandViaRMI(newCommand, this);
            } else {
                final IPCMechanism ipcMechanism = (IPCMechanism) newProposal;
                this.proxyServer.receiveIPCMechanismViaRMI(ipcMechanism, this);
            }
        } catch (RemoteException ex) {
            ex.printStackTrace();
        }
    }

    protected void sendCommandRmi(final String newCommand) {
        this.sendProposalViaRMI(newCommand, CommunicationStateNames.COMMAND);
    }

    protected void sendMechanismIpc(final IPCMechanism ipcMechanism) {
        this.sendProposalViaRMI(ipcMechanism, CommunicationStateNames.IPC_MECHANISM);
    }

    @Override
    public void ipcMechanism(final IPCMechanism newValue) {
        super.ipcMechanism(newValue);

    }

    @Override
    public void simulationCommand(final String newCommand) {
        super.simulationCommand(newCommand);
        final long delayTime = this.getDelay();
        if (delayTime > 0) {
            ThreadSupport.sleep(delayTime);
        }
        this.thisProcessor.setInputString(newCommand);
        this.sendCommandRmi(newCommand);
    }


    @Override
    public void localProcessingOnly(final boolean newValue) {
        super.localProcessingOnly(newValue);
        if (this.isLocalProcessingOnly()) {
            this.thisProcessor.removePropertyChangeListener(coupler);
        } else {
            this.thisProcessor.addPropertyChangeListener(coupler);
        }
    }

    @Override
    public void experimentInput() {
        long start = System.currentTimeMillis();
        PerformanceExperimentStarted.newCase(this, start, commands);
        final boolean oldTrace = isTrace();
        this.trace(false);
        for (int i = 0; i < commands; i++) {
            this.simulationCommand(tryCommand);
        }
        this.trace(oldTrace);
        final long end = System.currentTimeMillis();
        PerformanceExperimentEnded.newCase(this, start, end, end - start, commands);
        
        
        System.out.println("Printing out the time:");
        System.out.println(end - start);
    }

    @Override
    public void exportClientProxy() {
        try {
            UnicastRemoteObject.exportObject(this, 0);
            this.rmiRegistry.rebind(RMIClientInterface.class.getName(), this);
            RMIObjectRegistered.newCase(this, RMIClientInterface.class.getName(), this, this.rmiRegistry);
        } catch (RemoteException ex) {
            ex.printStackTrace();
        }
    }

    
    private HalloweenCommandProcessor getProcessor() {
    	return this.thisProcessor;
    }
    protected void receiveNotifications(final String object, final Object newProposal) {
        ProposalLearnedNotificationReceived.newCase(this, CommunicationStateNames.BROADCAST_MODE, -1, object);
        ProposedStateSet.newCase(this, object, -1, newProposal);
        if (object.equals(CommunicationStateNames.COMMAND)) {
            final String newCommand = (String) newProposal;
//            RMIClient.getSingleton();
			final HalloweenCommandProcessor thisProcessor = getProcessor();
//            System.out.println("?????????????!!!!!!!!!!!!!!!");
//            System.out.println(newCommand);
//            System.out.println(thisProcessor);
            thisProcessor.setInputString(newCommand);
        } else {
            IPCMechanism ipcMechanism = (IPCMechanism) newProposal;
            ((AnAbstractSimulationParametersBean) RMIClient.getSingleton()).setIPCMechanism(ipcMechanism);
        }
    }

    @Override
    public void receiveNotificationsViaRMI(final String object, final Object newProposal) {
        this.receiveNotifications(object, newProposal);
    }

    @Override
    public void locateRegistry(final int port, final String host) {
        try {
            this.rmiRegistry = LocateRegistry.getRegistry(port);
            RMIRegistryLocated.newCase(this, host, port, this.rmiRegistry);
        } catch (RemoteException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void lookupServerProxy() {
        try {
            this.proxyServer = (RMIServerInterface) this.rmiRegistry.lookup(RMIServerInterface.class.getName());
            RMIObjectLookedUp.newCase(this, this.proxyServer, RMIServerInterface.class.getName(), rmiRegistry);
        } catch (RemoteException | NotBoundException ex) {
            ex.printStackTrace();
        }
    }


}
