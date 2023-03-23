package registry;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;

import util.annotations.Tags;
import util.tags.DistributedTags;

import  util.trace.port.rpc.rmi.RMIRegistryCreated;
import assignments.util.mainArgs.RegistryArgsProcessor;

@Tags({DistributedTags.REGISTRY, DistributedTags.RMI})
public class MyRegistry {
	//TODO get rid of hard coded port number
	 private static int SERVER_PORT = 1099;

	public static void main (final String[] args) {
		SERVER_PORT = RegistryArgsProcessor.getRegistryPort(args);
		 try {
			final Registry rmiRegistry = LocateRegistry.createRegistry(SERVER_PORT);
			Scanner scanner = new Scanner(System.in);
		    scanner.nextLine();

			
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		 
	 }

}
