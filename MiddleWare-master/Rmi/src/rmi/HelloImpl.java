package rmi;
import org.jgrapht.*;
import org.jgrapht.alg.connectivity.*;
import org.jgrapht.alg.interfaces.ShortestPathAlgorithm.*;
import org.jgrapht.alg.interfaces.*;
import org.jgrapht.alg.shortestpath.*;
import org.jgrapht.graph.*;
import java.util.*;

public class HelloImpl implements Hello {
    private Graph<String, DefaultEdge> directedGraph =
            new DefaultDirectedGraph<String, DefaultEdge>(DefaultEdge.class);

    public HelloImpl() {
        directedGraph.addVertex("a");
        directedGraph.addVertex("b");
        directedGraph.addVertex("c");
        directedGraph.addVertex("d");
        directedGraph.addVertex("e");
        directedGraph.addVertex("f");
        directedGraph.addVertex("g");
        directedGraph.addEdge("a", "b");
        directedGraph.addEdge("b", "d");
        directedGraph.addEdge("d", "c");
        directedGraph.addEdge("c", "a");
        directedGraph.addEdge("e", "d");
        directedGraph.addEdge("e", "f");
        directedGraph.addEdge("f", "g");
        directedGraph.addEdge("g", "e");
        directedGraph.addEdge("a", "e");
        directedGraph.addEdge("d", "f");
    }

    public String sayHello(String name) {
        return "rmi.Hello, " + name + " !";
    }
    public List<Graph<String, DefaultEdge>> getGraph() {
        // computes all the strongly connected components of the directed graph
        StrongConnectivityAlgorithm<String, DefaultEdge> scAlg = new KosarajuStrongConnectivityInspector<>(directedGraph);
        return scAlg.getStronglyConnectedComponents();
    }

    public Integer getPoint() {
        return directedGraph.vertexSet().size();
    }

    public Integer getEdge() {
        return directedGraph.edgeSet().size();
    }

    public Boolean addPoint(String name) {
        return directedGraph.addVertex(name);
    }

    public DefaultEdge addEdge(String startPoint, String endPoint) {
        return directedGraph.addEdge(startPoint, endPoint);
    }

    public Boolean deletePoint(String name) {
        return directedGraph.removeVertex(name);
    }

    public GraphPath<String, DefaultEdge> getShortest(String startPoint, String endPoint) {
        // Prints the shortest path from vertex i to vertex c. This certainly
        // exists for our particular directed graph.
        DijkstraShortestPath<String, DefaultEdge> dijkstraAlg =
                new DijkstraShortestPath<>(directedGraph);
        SingleSourcePaths<String, DefaultEdge> iPaths = dijkstraAlg.getPaths(startPoint);
        return iPaths.getPath(endPoint);
    }
}