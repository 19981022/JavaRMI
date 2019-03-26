package rmi;
import org.jgrapht.*;
import org.jgrapht.graph.*;
import java.util.*;

public interface Hello extends java.rmi.Remote {
    // constructs a directed graph with the specified vertices and edges
    String sayHello(String name) throws java.rmi.RemoteException;

    List<Graph<String, DefaultEdge>> getGraph() throws java.rmi.RemoteException;
    Integer getPoint() throws java.rmi.RemoteException;
    Integer getEdge() throws java.rmi.RemoteException;
    Boolean addPoint(String name) throws java.rmi.RemoteException;
    DefaultEdge addEdge(String startPoint, String endPoint) throws java.rmi.RemoteException;
    Boolean deletePoint(String name) throws java.rmi.RemoteException;
    GraphPath<String, DefaultEdge> getShortest(String startPoint, String endPoint) throws java.rmi.RemoteException;
}