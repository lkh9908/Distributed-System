package comp533.registry;

import assignments.util.mainArgs.RegistryArgsProcessor;
import util.annotations.Tags;
import util.tags.DistributedTags;
import util.trace.port.rpc.rmi.RMIRegistryCreated;
import util.trace.port.rpc.rmi.RMITraceUtility;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;


@Tags({DistributedTags.REGISTRY, DistributedTags.RMI})
public class MyRegistry {
    public static void main(String[] args) {
        try {
            RMITraceUtility.setTracing();
            int port = RegistryArgsProcessor.getRegistryPort(args);
            final Registry registry = LocateRegistry.createRegistry(port);
            RMIRegistryCreated.newCase(registry, port);
            @SuppressWarnings("resource")
			Scanner scanner = new Scanner(System.in);
            scanner.nextLine();
        } catch (RemoteException ex) {
            ex.printStackTrace();
        }
    }

}
