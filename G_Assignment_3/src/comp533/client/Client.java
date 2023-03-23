package comp533.client;


import comp533.reducer.ReducerInterface;
import comp533.reducer.ReducerFactory;
import comp533.keyValue.KeyValueInterface;
import comp533.main.TokenCounterModel;
import comp533.main.RemoteModel;
import comp533.main.ModelInterface;
import gradingTools.comp533s19.assignment0.AMapReduceTracer;
import util.trace.Tracer;


import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import java.util.Map;

public class Client extends AMapReduceTracer implements ClientInterface {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static void main(String[] args) {
        try {
            Registry rmiRegistry = LocateRegistry.getRegistry(1091);
            Client remoteClient = new Client();
            UnicastRemoteObject.exportObject(remoteClient, 0);
            rmiRegistry.rebind(Client.class.getName(), remoteClient);
            
            RemoteModel counter1 = (RemoteModel) rmiRegistry.lookup(RemoteModel.class.getName());
            counter1.registerClient(remoteClient);
            
            System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            System.out.println(counter1.toString());
            System.out.println(remoteClient.toString());
            System.out.println("?????????????????????????????????");
            
            remoteClient.synchronizedWait();
            AMapReduceTracer.traceExit(ModelInterface.class);
            System.exit(0);
        } catch (RemoteException | NotBoundException | InterruptedException ex) {
            ex.printStackTrace();
        }
    }

	public Map<String, Integer> reduce(List<KeyValueInterface<String, Integer>> serializableKeyValuePairs) throws RemoteException {
    	this.trace("lalallaallalallaalallaalala");
        this.traceRemoteList(serializableKeyValuePairs);
        this.trace("bababababababbabababababababba");
        ReducerInterface<String, Integer> reducer = ReducerFactory.getReducer();
        Map<String, Integer> result = reducer.reduce(serializableKeyValuePairs);
        this.traceRemoteResult(result);
        return result;
    }

    public void quit() {
        this.traceQuit();
        this.synchronizedNotify();
    }

	


}
