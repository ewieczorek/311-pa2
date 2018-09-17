import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {
	// write your code here
        ArrayList<String> tester = new ArrayList<>();
        //tester.add("Iowa State");
        //tester.add("Cyclones");

        //WikiCrawler w1 = new WikiCrawler("/wiki/Computer_Science", 100, tester, "output.txt");
        //w1.crawl();

        WikiCrawler w1 = new WikiCrawler("/wiki/Computer_Science", 100, tester, "wikiCS.txt");
        w1.crawl();

        NetworkInfluence ni = new NetworkInfluence("output.txt");
//        System.out.println(ni.distance("/wiki/Simplicity", "/wiki/Complexity_economics"));
//        ni.shortestPath("/wiki/Simplicity", "/wiki/Complexity_economics");
        //System.out.println("Simplicity influence " + ni.influence("/wiki/Simplicity"));
        //ArrayList<String> a1 = new ArrayList<>();
        //a1.add("/wiki/Simplicity");
        //System.out.println(ni.influence(a1));
        System.out.println("\nDegree greedy: ");
        ArrayList<String> degreeGreedy = ni.mostInfluentialDegree(10);
        System.out.println("");
        for(String s: degreeGreedy){
            System.out.println(s);
        }


        System.out.println("\n\nModular greedy: ");
        ArrayList<String> modularGreedy = ni.mostInfluentialModular(10);
        System.out.println("");
        for(String s: modularGreedy){
            System.out.println(s);
        }



        System.out.println("\n\nSubModular greedy: ");
        ArrayList<String> subModularGreedy = ni.mostInfluentialSubModular(10);
        System.out.println("");
        for(String s: subModularGreedy){
            System.out.println(s);
        }
    }
}
