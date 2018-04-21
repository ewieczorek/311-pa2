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
    private HashMap<String, ArrayList<String>> graph;
    //File name of the graph data
    private String graphData;

    // NOTE: graphData is an absolute file path that contains graph data, NOT the raw graph data itself
    public NetworkInfluence(String graphData)
    {
        this.graphData = graphData;
        this.graph = new HashMap<>();
        int fileError = createGraphFromFile();
        if (fileError == 1){
            System.out.println("Error with the file");
        }

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
                    queue.add(str);
                }
            }
            visited.add(temp);
        }
        if(distance.containsKey(v)) {
            distance.get(v).add(v);
//Debug print lines
//            for(String str: distance.get(v)){
//                System.out.println(str + ",");
//            }
            return distance.get(v);
        }
        return new ArrayList<String>();
    }

    /*
    distance(String u, String v). Returns dist(u, v). Type is int.
     */
    public int distance(String u, String v)
    {
        if(u.equals(v)) return 0;
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
                    queue.add(str);
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
                if (distance < leastDistance && distance != -1) {
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
        Set<String> valuesToCheck = new HashSet<>();
        for(ArrayList<String> al: graph.values()){
            for(String str: al){
                valuesToCheck.add(str);
            }
        }
        for(String str: valuesToCheck){
            int dist = distance(u, str);
            if(dist != -1) {
                values[dist]++;
            }
        }

        for(int i = 0; i < values.length; i++){
            inf += ((1/(Math.pow(2, i))) * values[i]);
        }
        return inf;
    }



    /*
    influence(ArrayList<String> s). Here s holds a set of vertices of G. This method returns
    Inf(s) whose type is float.

    So I'm not sure if I followed the instructions correctly here but this is what it does:
    Goes through every node that exists in the set of all values and stores them in a set called valuesToCheck
    goes through every value in valuesToCheck and finds its MINIMUM distance from any vertex in the ArrayList
    For every minimum distance it add one to the values array at the index of it's distance

    Computes the inf using the values array. Every index in the array corresponds to how many nodes are at that distance.
     */
    public float influence(ArrayList<String> s)
    {

        float inf = 0;
        int[] values = new int[graph.size()];
        Arrays.fill(values, 0, values.length, 0);
        Set<String> valuesToCheck = new HashSet<>();
        for(ArrayList<String> al: graph.values()){
            for(String str: al){
                valuesToCheck.add(str);
            }
        }
        for(String str: valuesToCheck){
            int dist = distance(s, str);
            if(dist != -1) {
                values[dist]++;
            }
        }

        for(int i = 0; i < values.length; i++){
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
        long startTime = System.currentTimeMillis();

        ArrayList<String> output = new ArrayList<>();
        List<Pair<String, Integer>> degree = new ArrayList<>();
        for(String str: graph.keySet()){
            int j = 0;
            if(degree.isEmpty()){
                degree.add(new Pair<String, Integer>(str, outDegree(str)));
            }else {
                boolean added = false;
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

        long endTime = System.currentTimeMillis();
        System.out.println("Total execution time: " + (endTime - startTime) );

        for(int i = 0; i < k; i++) {
            output.add(degree.get(i).getKey());
            System.out.println(degree.get(i).getKey() + " degree: " + degree.get(i).getValue() + " Influence: " + influence(degree.get(i).getKey()));
        }

        return output;
    }

    /*
    mostInfluentialModular(int k). Returns a set of k nodes obtained by using Modular Greedy
    algorithm. Return type must be ArrayList<String>.
     */
    public ArrayList<String> mostInfluentialModular(int k)
    {
        long startTime = System.currentTimeMillis();

        ArrayList<String> output = new ArrayList<>();
        List<Pair<String, Float>> modular = new ArrayList<>();
        for(String str: graph.keySet()){
            int j = 0;
            float inf = influence(str);
            if(modular.isEmpty()){
                modular.add(new Pair<String, Float>(str, inf));
            }else {
                boolean added =false;
                for (Pair<String, Float> p : modular) {
                    if (p.getValue() != null && inf >= p.getValue()) {
                        modular.add(j, new Pair<String, Float>(str, inf));
                        added = true;
                        break;
                    }
                    j++;
                }
                if(!added) modular.add(new Pair<String, Float>(str, inf));
            }
        }

        long endTime = System.currentTimeMillis();
        System.out.println("Total execution time: " + (endTime - startTime) );

        for(int i = 0; i < k; i++) {
            output.add(modular.get(i).getKey());
            System.out.println(modular.get(i).getKey() + " influence: " + modular.get(i).getValue());
        }

        return output;
    }

    /*
    mostInfluentialSubModular(int k). Returns a set of k nodes obtained by using SubModular
    Greedy algorithm. Return type must be ArrayList<String>.
     */
    //TODO
    public ArrayList<String> mostInfluentialSubModular(int k)
    {
        long startTime = System.currentTimeMillis();

        ArrayList<String> u = new ArrayList<>();
        for(int i = 0; i < k; i++) {
            ArrayList<String> u1 = new ArrayList<>();
            u1.addAll(u);
            u1.add(graph.entrySet().iterator().next().getKey());
            for (String s2 : graph.keySet()) {
                ArrayList<String> v = new ArrayList<>();
                v.addAll(u);
                v.add(s2);
                if (influence(v) > influence(u1)) {
                    u1 = v;
                }
            }
            u = new ArrayList<>();
            u.addAll(u1);
        }
        long endTime = System.currentTimeMillis();
        System.out.println("Total execution time: " + (endTime - startTime) );

        System.out.println("Influence of the group: " + influence(u));
        for(String str: u) {
            System.out.println(str + " influence: " + influence(str));
        }

        return u;
    }

    private int createGraphFromFile(){
        try {
            BufferedReader br = new BufferedReader(new FileReader(this.graphData));
            String line = "";
            while((line = br.readLine()) != null){
                if(!line.matches("\\d+")){
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