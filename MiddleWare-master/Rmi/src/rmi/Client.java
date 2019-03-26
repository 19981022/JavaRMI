package rmi;
import java.util.Scanner;
import java.net.MalformedURLException;
import java.rmi.registry.*;
import java.rmi.*;
import org.jgrapht.*;
import org.jgrapht.graph.*;
import java.util.*;

public class Client {
    private Client() {
    }

    public static void main(String[] args) throws RemoteException, MalformedURLException, NotBoundException {
        String host = (args.length < 1) ? "localhost" : args[0];
        String name = (args.length == 2) ? args[1] : "World";

        String urlo = "rmi://" + host + ":3333/Hello";
        Hello stub = (Hello) Naming.lookup(urlo);
        System.out.println("link to the server: \n" + urlo);
        Registry registry = LocateRegistry.getRegistry(host);
        String response = stub.sayHello(name);
        System.out.println("Response: " + response);

        try (Scanner scan = new Scanner(System.in)) {
            String s;
            String startPoint = null;
            String endPoint = null;
            String removePoint = null;
            String newPoint = null;
            label:
            while (true) {
                System.out.println("Enter command:\n" +
                        "'s' to show the default graph\n" +
                        "'p' to show the point sum\n" +
                        "'e' to show the edge sum\n" +
                        "'+p' to add a point\n" +
                        "'+e' to add an edge\n" +
                        "'-p' to remove a point\n" +
                        "'d' to show the shortest path between 2 points\n" +
                        "'q' to quit.");
                s = scan.nextLine();
                switch (s) {
                    case "s":
                        List<Graph<String, DefaultEdge>> stronglyConnectedSubgraphs = stub.getGraph();
                        System.out.println("Strongly connected components:");
                        for (Graph<String, DefaultEdge> stronglyConnectedSubgraph : stronglyConnectedSubgraphs) {
                            System.out.println(stronglyConnectedSubgraph);
                        }
                        break;
                    case "p":
                        System.out.println("Point Sum:" + stub.getPoint());
                        break;
                    case "e":
                        System.out.println("Edge Sum:" + stub.getEdge());
                        break;
                    case "+p":
                        System.out.println("Enter new point name: ");
                        newPoint = scan.nextLine();
                        System.out.println("Add Point:" + stub.addPoint(newPoint));
                        break;
                    case "+e":
                        System.out.println("Enter new edge's start point: ");
                        startPoint = scan.nextLine();
                        System.out.println("Enter new edge's end point: ");
                        endPoint = scan.nextLine();
                        System.out.println("Add Edge:" + stub.addEdge(startPoint, endPoint));
                        break;
                    case "-p":
                        System.out.println("Enter remove point name: ");
                        removePoint = scan.nextLine();
                        System.out.println("Remove Point:" + stub.deletePoint(removePoint));
                        break;
                    case "d":
                        System.out.println("Enter start point: ");
                        startPoint = scan.nextLine();
                        System.out.println("Enter end point: ");
                        endPoint = scan.nextLine();
                        System.out.println("Shortest Path:" + stub.getShortest(startPoint, endPoint));
                        break;
                    case "q":
                        break label;
                    default:
                        System.out.println("command not found");
                        break;
                }
            }
        }
    }
}