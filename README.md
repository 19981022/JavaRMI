# <center>RPC原理和java RMI实践教程<center>

## 实验环境
操作系统：Windows10 Pro
<br>开发环境：IDEA
<br>语言: Java
<br>JDK版本: 1.8.0_202

## 实验目的
掌握远程过程调用原理，基于java RMI进行远程编程和控制。要求定义远程接口类及实现类


## 什么是RPC？
RPC 是远程过程调用（Remote Procedure Call）的缩写形式。RPC 是指计算机 A 上的进程，调用另外一台计算机 B 上的进程，其中 A 上的调用进程被挂起，而 B 上的被调用进程开始执行，当值返回给 A 时，A 进程继续执行。调用方可以通过使用参数将信息传送给被调用方，而后可以通过传回的结果得到信息。而这一过程，对于开发人员来说是透明的。
图1 描述了数据报在一个简单的RPC传递的过程


![Alt text](https://waylau.com/images/post/20160630-rpc.png)
### <center>图1 简单的RPC过程<center>
远程过程调用采用客户机/服务器(C/S)模式。请求程序就是一个客户机，而服务提供程序就是一台服务器。和常规或本地过程调用一样，远程过程调用是同步操作，在远程过程结果返回之前，需要暂时中止请求程序。使用相同地址空间的低权进程或低权线程允许同时运行多个远程过程调用。

## 基于java RMI进行远程编程和控制
- ### 什么是RMI？
  RMI（Remote Method Invocation):远程方法调用，即在RPC的基础上有向前迈进了一步，提供分布式对象间的通讯。允许运行在一个java 虚拟机的对象调用运行在另一个java虚拟机上对象的方法。这两个虚拟机可以是运行在相同计算机上的不同进程中，也可以是运行在网络上的不同计算机中。
- ### RMI的工作原理
    #### 从设计角度上讲，JAVA采用的是三层结构模式来实现RMI。在整个体系结构中，有如下几个关键角色构成了通信双方：
    + 1.**客户端**：
  
        (1). 桩(StubObject)：远程对象在客户端上的代理；

        (2). 远程引用层(RemoteReference Layer)：解析并执行远程引用协议；

        (3). 传输层(Transport)：发送调用、传递远程方法参数、接收远程方法执行结果。
    + 2.**服务端**

        (1). 骨架(Skeleton)：读取客户端传递的方法参数，调用服务器方的实际对象方法，并接收方法执行后的返回值；

        (2). 远程引用层(Remote ReferenceLayer)：处理远程引用语法之后向骨架发送远程方法调用；

        (3). 传输层(Transport)：监听客户端的入站连接，接收并转发调用到远程引用层。
    + 3.**注册表(Registry)**:
        以URL形式注册远程对象，并向客户端回复对远程对象的引用。

        ![Alt text](http://dl2.iteye.com/upload/attachment/0084/9211/e2c85080-e019-391d-a32a-9fef0ef35b20.png)

### <center>图2 RMI三层模型<center>
- ### RMI的使用
  在这一节，我们将从头开始写一个简单的RMI示例，实现服务器端构建一个图结构，然后客户端可以往这个图插入节点和边，可删除节点（附带删除边），并可查询图的节点数、边数，以及任意两个边的最短路径。
  
    **创建一个完整的远程调用，我们分为以下几步**：
    >(1). 定义一个远程接口，这个接口需要继承Remote，并且接口中的每一个方法都需要抛出RemoteException异常
    ><br>(2). 开发远程接口的实现类
    ><br>(3). Registry的创建
    ><br>(4). RMI Server的创建
    ><br>(5). RMI Client的创建

    ###  **定义一个远程接口**
    ```java
    package rmi;
    import org.jgrapht.*;
    import org.jgrapht.graph.*;
    import java.util.*;

    public interface Hello extends java.rmi.Remote {
    //构建一个确定的图
    List<Graph<String, DefaultEdge>> getGraph() throws java.rmi.RemoteException;
    //获取图
    Integer getPoint() throws java.rmi.RemoteException;
    //获取点
    Integer getEdge() throws java.rmi.RemoteException;
    //获取一条边
    Boolean addPoint(String name) throws java.rmi.RemoteException;
    //增加一个点
    DefaultEdge addEdge(String startPoint, String endPoint) throws java.rmi.RemoteException;
    //增加一条边
    Boolean deletePoint(String name) throws java.rmi.RemoteException;
    //删除一个点
    GraphPath<String, DefaultEdge> getShortest(String startPoint, String endPoint) throws java.rmi.RemoteException;
    //得到最短路径
    }
    ```

    
    ###  **开发远程接口的实现类**
    ```java
    package rmi;
    import org.jgrapht.*;
    import org.jgrapht.alg.connectivity.*;
    import org.jgrapht.alg.interfaces.ShortestPathAlgorithm.*;
    import org.jgrapht.alg.interfaces.*;
    import org.jgrapht.alg.shortestpath.*;
    import org.jgrapht.graph.*;
    import java.util.*;

    public class HelloImpl implements Hello 
    {
    private Graph<String, DefaultEdge> directedGraph =
            new DefaultDirectedGraph<String, DefaultEdge>(DefaultEdge.class);

    public HelloImpl() 
    {
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
        
    }
    //构建默认图
    public String sayHello(String name) {
        return "rmi.Hello, " + name + " !";
    }

    public List<Graph<String, DefaultEdge>> getGraph()
    {
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

    public GraphPath<String, DefaultEdge> getShortest(String startPoint, String endPoint) 
    {
        // Prints the shortest path from vertex i to vertex c. This certainly
        // exists for our particular directed graph.
        DijkstraShortestPath<String, DefaultEdge> dijkstraAlg =
                new DijkstraShortestPath<>(directedGraph);
        SingleSourcePaths<String, DefaultEdge> iPaths = dijkstraAlg.getPaths(startPoint);
        return iPaths.getPath(endPoint);
    }   
    }
    ```



    ### **Registry和RMI Server 的创建**
    ```java
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
    
    ```
    ### **RMI Client的创建**
    ```java
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
    ```

## 结果截图



      
  









### reference[https://blog.csdn.net/bryan__/article/details/47114165?from=groupmessage&isappinstalled=0]




