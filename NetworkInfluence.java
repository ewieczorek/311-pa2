/**
 * @author Ethan Wieczorek and Dalton Sherratt
 */
// LEAVE THIS FILE IN THE DEFAULT PACKAGE
//  (i.e., DO NOT add 'package cs311.pa1;' or similar)

// DO NOT MODIFY THE EXISTING METHOD SIGNATURES
//  (you may, however, add member fields and additional methods)

// DO NOT INCLUDE LIBRARIES OUTSIDE OF THE JAVA STANDARD LIBRARY
//  (i.e., you may only include libraries of the form java.*)


import javafx.util.Pair;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class NetworkInfluence
{
//    private int edges; //number of edges
//    private int verts;  //number of vertices
//    private int numIter;
    private HashMap<String, ArrayList<String>> graph;
    //private HashMap<String, ArrayList<String>> UtoV; //out degree of vertices
    //private HashMap<String, ArrayList<String>> VtoU; //in degree of vertices
    //private HashMap<String, Double> influenceGraph;
    //private Set<String> numVerts;        //list of vertices
    //File name of the graph data
    private String graphData;
    //Nodes that have already been checked while computing distance.

    // NOTE: graphData is an absolute file path that contains graph data, NOT the raw graph data itself
    //TODO
    public NetworkInfluence(String graphData)
    {
        this.graphData = graphData;
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
    public ArrayList<String> shortestPath(String u, String v)
    {
        LinkedList<String> queue = new LinkedList<>();
        HashMap<String, ArrayList<String>> distance = new HashMap<>();
        Set<String> visited = new HashSet<>();

        queue.add(u);

        visited.add(u);

        ArrayList<String> al1 = new ArrayList<>();
        al1.add(u);
        distance.put(u, al1);

        while(queue.size()!=0){
            String temp = queue.poll();
            if(graph.get(temp)!=null) {
                for (String str : graph.get(temp)) {
                    if (visited.contains(str)) continue;

                    if (distance.containsKey(str)) {
                        distance.get(str).addAll(distance.get(temp));
                    } else {
                        ArrayList<String> al = new ArrayList<>();
                        al.addAll(distance.get(temp));
                        if(!distance.get(temp).contains(temp)){
                            al.add(temp);

                        }
                        distance.put(str, al);
                    }
                    queue.push(str);
                    visited.add(str);
                }
            }
        }
        distance.get(v).add(v);
        if(distance.containsKey(v)) {
            for(String str: distance.get(v)){
                System.out.println(str + ",");
            }
            return distance.get(v);
        }
        return null;
    }

    /*
    distance(String u, String v). Returns dist(u, v). Type is int.
     */
    public int distance(String u, String v)
    {
        LinkedList<String> queue = new LinkedList<>();
        HashMap<String, Integer> distance = new HashMap<>();
        Set<String> visited = new HashSet<>();

        queue.add(u);

        visited.add(u);
        while(queue.size()!=0){
            String temp = queue.poll();
            if(graph.get(temp)!=null) {
                for (String str : graph.get(temp)) {
                    if (visited.contains(str)) continue;

                    if (distance.containsKey(temp)) {
                        distance.put(str, distance.get(temp) + 1);
                    } else {
                        distance.put(str, 1);
                    }
                    queue.push(str);
                    visited.add(str);
                }
            }
        }
        if(distance.containsKey(v)) return distance.get(v);
        return -1;
    }


    /*
    distance(ArrayList<String> s, String v). Here s a subset of vertices. This method returns
    dist(s, v). Type is int.
     */
    public int distance(ArrayList<String> s, String v)
    {
        if(!s.isEmpty()) {
            int leastDistance = distance(s.get(0), v);
            for (String str : s) {
                int distance = distance(str, v);
                if (distance < leastDistance) {
                    leastDistance = distance;
                }
            }
            return leastDistance;
        }
        return -1;
    }

    /*
    influence(String u). Returns Inf(x). Return type is float.
     */
    public float influence(String u)
    {
        // implementation
        float inf = 0;
        int[] values = new int[graph.size()];
        Arrays.fill(values, 0, values.length, 0);

        for(String str: graph.keySet()){
            if(!str.equals(u)){
                int dist = distance(u, str);
                if(dist != -1) {
                    values[dist]++;
                }
            }
        }

        for(int i: values){
            inf += ((1/(Math.pow(2, i))) * values[i]);
        }
        return inf;
    }



    /*
        float dist = distance(graph.get(Vertex), y)
     */



    /*
    influence(ArrayList<String> s). Here s holds a set of vertices of G. This method returns
    Inf(s) whose type is float.
     */
    public float influence(ArrayList<String> s)
    {
        float inf = 0;
        int[] values = new int[graph.size()];
        Arrays.fill(values, 0, values.length, 0);

        for(String str: graph.keySet()){
            int dist = distance(s, str);
            if(dist != -1) {
                values[dist]++;
            }
        }

        for(int i: values){
            inf += ((1/(Math.pow(2, i))) * values[i]);
        }
        return inf;
    }

    /*
    mostInfluentialDegree(int k). Returns a set of k nodes obtained by using Degree Greedy
    algorithm. Return type must be ArrayList<String>.
     */
    //TODO
    public ArrayList<String> mostInfluentialDegree(int k)
    {
        ArrayList<String> output = new ArrayList<>();
        HashMap<String, Integer> degrees = new HashMap<>();
        List<Pair<String, Integer>> degree = new ArrayList<>();
        for(String str: graph.keySet()){
            degrees.put(str, outDegree(str));
            int j = 0;
            if(degree.isEmpty()){
                degree.add(new Pair<String, Integer>(str, outDegree(str)));
            }else {
                boolean added =false;
                int out = outDegree(str);
                for (Pair<String, Integer> p : degree) {
                    if (p.getValue() != null && out >= p.getValue()) {
                        degree.add(j, new Pair<String, Integer>(str, out));
                        added = true;
                        break;
                    }
                    j++;
                }
                if(!added) degree.add(new Pair<String, Integer>(str, out));
            }
        }

        for(int i = 0; i < k; i++) {
            output.add(degree.get(i).getKey());
        }


//
//
//        for(int i = degreeList.size(); i > 0; i--) {
//            if(degreeList.get(i) != null && !degreeList.get(i).isEmpty()){
//                for(String s: degreeList.get(i)){
//                    if(output.size() < k){
//                        output.add(s);
//                    }else{
//                        return output;
//                    }
//                }
//            }
//        }
        return output;
    }

    /*
    mostInfluentialModular(int k). Returns a set of k nodes obtained by using Modular Greedy
    algorithm. Return type must be ArrayList<String>.
     */
    //TODO
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
    //TODO
    public ArrayList<String> mostInfluentialSubModular(int k)
    {
        // implementation

        // replace this:
        return null;
    }

    private int createGraphFromFile(){
        try {
            BufferedReader br = new BufferedReader(new FileReader(this.graphData));
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