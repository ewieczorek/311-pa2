// LEAVE THIS FILE IN THE DEFAULT PACKAGE
//  (i.e., DO NOT add 'package cs311.pa1;' or similar)

// DO NOT MODIFY THE EXISTING METHOD SIGNATURES
//  (you may, however, add member fields and additional methods)

// DO NOT INCLUDE LIBRARIES OUTSIDE OF THE JAVA STANDARD LIBRARY
//  (i.e., you may only include libraries of the form java.*)

/**
 * @author Hugh Potter
 */

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.*;

public class NetworkInfluence
{
    private int edges; //number of edges
    private int verts;  //number of vertices
    private int numIter;
    private HashMap<String, ArrayList<String>> graph;
    private HashMap<String, ArrayList<String>> UtoV; //out degree of vertices
    private HashMap<String, ArrayList<String>> VtoU; //in degree of vertices
    private HashMap<String, Double> influenceGraph;
    private Set<String> numVerts;        //list of vertices
    //File name of the graph data
    private String graphdata;
    //Nodes that have already been checked while computing distance.
    private Set<String> distanceCheckedNodes;
    private int lowestDistanceFound;

    // NOTE: graphData is an absolute file path that contains graph data, NOT the raw graph data itself
    public NetworkInfluence(String graphData)
    {
        this.graphdata = graphData;
        this.graph = new HashMap<>();
        int fileError = createGraphFromFile();
        if (fileError == 1){
            System.out.println("Error with the file");
        }

        // implementation
    }

    /*
    outDegree(String v) Returns the out degree of v.
     */
    public int outDegree(String v)
    {
        if(graph.containsKey(v)){
            return graph.get(v).size();
        }else{
            return 0;
        }
    }

    /*
    shortestPath(String u, string v) Returns a BFS path from u to v. This method returns an array
    list of strings that represents a shortest path from u to v. Note that this method must return an
    array list of Strings. First vertex in the path must be u and the last vertex must be v. If there is no
    path from u to v, then this method returns an empty list. The return type is ArrayList<String>
     */
    //TODO
    public ArrayList<String> shortestPath(String u, String v)
    {


//        private Map<Node, Boolean>> vis = new HashMap<Node, Boolean>();
//
//        private Map<Node, Node> prev = new HashMap<Node, Node>();
//
//        public ArrayList path(Node start, Node finish){
//        List directions = new LinkedList();
//        Queue q = new LinkedList();
//        Node current = start;
//        q.add(current);
//        vis.put(current, true);
//        while(!q.isEmpty()){
//            current = q.remove();
//            if (current.equals(finish)){
//                break;
//            }else{
//                for(Node node : current.getOutNodes()){
//                    if(!vis.contains(node)){
//                        q.add(node);
//                        vis.put(node, true);
//                        prev.put(node, current);
//                    }
//                }
//            }
//        }
//        if (!current.equals(finish)){
//            System.out.println("can't reach destination");
//        }
//        for(Node node = finish; node != null; node = prev.get(node)) {
//            directions.add(node);
//        }
//        path.reverse();
//        return path;
//    }
        return null;
    }

    /*
    distance(String u, String v). Returns dist(u, v). Type is int.
     */
    public int distance(String u, String v)
    {
        ArrayList<String> temp = new ArrayList<>();
        temp.add(v);
        recDistance(u, v, temp, 0);
        return checkForNode(u,v,0);
    }

    private ArrayList<String> recDistance(String startNode, String finalNode, ArrayList<String> nodesToCheck, int distance){



        ArrayList<String> temp = new ArrayList<>();
        for(ArrayList<String> al: graph.values()){

        }

        return temp;
    }

    private int checkForNode(String u, String v, int distance){
        int distanceFound = 0;
        LinkedList<String> queue = new LinkedList<>();
        HashMap<String, Integer> visitedNodes = new HashMap<>();

        queue.add(u);
        visitedNodes.put(u, 0);
        while(queue.size()!=0){
            String temp = queue.poll();
            for(int i = 0; i < graph.get(temp).size(); i++){

            }
        }

        return distanceFound;
    }

    /*
    distance(ArrayList<String> s, String v). Here s a subset of vertices. This method returns
    dist(s, v). Type is int.
     */
    public int distance(ArrayList<String> s, String v)
    {
        int leastDistance = distance(s.get(0), v);
        for(String str: s){
            int distance = distance(str, v);
            if(distance < leastDistance){
                leastDistance = distance;
            }
        }
        return leastDistance;
    }

    /*
    influence(String u). Returns Inf(x). Return type is float.
     */
    public float influence(String u)
    {
        // implementation

        // replace this:
        return -1f;
    }

    /*
    influence(ArrayList<String> s). Here s holds a set of vertices of G. This method returns
    Inf(s) whose type is float.
     */
    public float influence(ArrayList<String> s)
    {
        // implementation

        // replace this:
        return -1f;
    }

    /*
    mostInfluentialDegree(int k). Returns a set of k nodes obtained by using Degree Greedy
    algorithm. Return type must be ArrayList<String>.
     */
    public ArrayList<String> mostInfluentialDegree(int k)
    {
        // implementation

        // replace this:
        return null;
    }

    /*
    mostInfluentialModular(int k). Returns a set of k nodes obtained by using Modular Greedy
    algorithm. Return type must be ArrayList<String>.
     */
    public ArrayList<String> mostInfluentialModular(int k)
    {
        // implementation

        // replace this:
        return null;
    }

    /*
    mostInfluentialSubModular(int k). Returns a set of k nodes obtained by using SubModular
    Greedy algorithm. Return type must be ArrayList<String>.
     */
    public ArrayList<String> mostInfluentialSubModular(int k)
    {
        // implementation

        // replace this:
        return null;
    }

    private int createGraphFromFile(){
        try {
            BufferedReader br = new BufferedReader(new FileReader(graphdata));
            String line = "";
            while((line = br.readLine()) != null){
                if(line.charAt(0) == '/'){
                    String str[];
                    str = line.split("\\s+");
                    if(graph.containsKey(str[0])){
                        graph.get(str[0]).add(str[1]);
                    }else{
                        ArrayList<String> temp = new ArrayList<>();
                        temp.add(str[1]);
                        graph.put(str[0], temp);
                    }
                }
            }
            return 0;
        } catch (FileNotFoundException e) {
            System.out.println("File not found");
            return 1;
        } catch (IOException e){
            System.out.println("Couldn't read file");
            return 1;
        }
    }
}