package rmi;
import java.rmi.*;
import java.rmi.registry.*;
import java.rmi.server.*;

@SuppressWarnings("ALL")
public class Server{
    public Server() {}
    public static void main(String args[]) {
        System.setSecurityManager(new RMISecurityManager());

        final HelloImpl obj = new HelloImpl();
        try {                               // 0 - anonymous TCP port ↓
            Hello stub = (Hello)UnicastRemoteObject.exportObject(obj, 0);
            // Bind the remote object's stub in the registry
            Registry registry = LocateRegistry.createRegistry(3333);
            registry.rebind("Hello", stub);
            for(int i = 0; i < registry.list().length; i++)
                System.out.println(registry.list()[i]);
            System.err.println("Server ready....");
            System.err.println("Listinging on port 3333 ....");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}