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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

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
    private String graphdata;

    // NOTE: graphData is an absolute file path that contains graph data, NOT the raw graph data itself
    public NetworkInfluence(String graphData)
    {
        this.graphdata = graphData;
        // implementation
    }

    /*
    outDegree(String v) Returns the out degree of v.
     */
    public int outDegree(String v)
    {

        return graph.get(v).size();
    }

    private int checkForNode(String v, String u, int distance){
        ArrayList<String> edges = new ArrayList<>();
        edges = graph.get(v);
        for(String str: edges){
            if(u.equals(str));
            return distance;
        }
        for(String str: edges){
            distance = checkForNode(str, u, distance + 1);
        }
        return distance;
    }

    /*
    shortestPath(String u, string v) Returns a BFS path from u to v. This method returns an array
    list of strings that represents a shortest path from u to v. Note that this method must return an
    array list of Strings. First vertex in the path must be u and the last vertex must be v. If there is no
    path from u to v, then this method returns an empty list. The return type is ArrayList<String>
     */
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
        return checkForNode(u,v,0);

    }

    /*
    distance(ArrayList<String> s, String v). Here s a subset of vertices. This method returns
    dist(s, v). Type is int.
     */
    public int distance(ArrayList<String> s, String v)
    {
        // implementation

        // replace this:
        return -1;
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

    private void createGraphFromFile(){
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
        } catch (FileNotFoundException e) {
            System.out.println("File not found");
        } catch (IOException e){
            System.out.println("Couldn't read file");
        }
    }
}