package facebook;


import comp533.reducer.ReducerInterface;
import comp533.reducer.ReducerFactory;
import comp533.client.ClientInterface;
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

public class RemoteClientFacebookMapReduce extends AMapReduceTracer implements ClientInterface {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static void main(String[] args) {
        try {
            Registry rmiRegistry = LocateRegistry.getRegistry(1092);
            RemoteClientFacebookMapReduce remoteClient = new RemoteClientFacebookMapReduce();
            UnicastRemoteObject.exportObject(remoteClient, 0);
            rmiRegistry.rebind(RemoteClientFacebookMapReduce.class.getName(), remoteClient);
            
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

	public Map<String, List<String>> reduceNew(List<KeyValueInterface<String, List<String>>> keyValuePairs) throws RemoteException {
    	this.trace("clientreducenew5555555555555555555555555555");
        this.traceRemoteList(keyValuePairs);
        this.trace("clientreducenew66666666666666666666666666");
        FacebookReducer reducer = new FacebookReducer();
        Map<String, List<String>> result = reducer.reduce(keyValuePairs);
        this.traceRemoteResult(result);
        return result;
    }

    public void quit() {
        this.traceQuit();
        this.synchronizedNotify();
    }

	@Override
	public Map<String, Integer> reduce(List<KeyValueInterface<String, Integer>> serializableKeyValuePairs)
			throws RemoteException {
		// TODO Auto-generated method stub
		this.trace("clientreducenew5555555555555555555555555555");
        this.traceRemoteList(serializableKeyValuePairs);
        this.trace("clientreducenew66666666666666666666666666");
		return null;
	}

}
