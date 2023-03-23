package comp533.server;

import comp533.controller.ControllerInterface;
import comp533.main.TokenCounterModel;
import comp533.main.RemoteModel;
import comp533.controller.Controller;
import comp533.view.View;
import comp533.view.ViewInterface;
import util.trace.Tracer;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class TokenCounterServer {
    private static Registry rmiRegistry;

    public static void main(String[] args) {
        try {
            rmiRegistry = LocateRegistry.createRegistry(1091);
            RemoteModel counter1 = new TokenCounterModel();
            UnicastRemoteObject.exportObject(counter1, 0);
            rmiRegistry.rebind(RemoteModel.class.getName(), counter1);
            ViewInterface view = new View();
            ControllerInterface controller = new Controller(counter1, view);
            controller.getUserInput(counter1);
            System.exit(0);
        } catch (RemoteException ex) {
            Tracer.error(ex.getMessage());
        }
    }
}
