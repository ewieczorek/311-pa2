import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {
	// write your code here
        ArrayList<String> tester = new ArrayList<>();
        //tester.add("Iowa State");
        //tester.add("Cyclones");

        //WikiCrawler w1 = new WikiCrawler("/wiki/Computer_Science", 100, tester, "output.txt");
        //w1.crawl();

        WikiCrawler w1 = new WikiCrawler("/wiki/Complexity_theory", 20, tester, "output.txt");
        w1.crawl();

        NetworkInfluence ni = new NetworkInfluence("output.txt");
        System.out.println(ni.distance("/wiki/Simplicity", "/wiki/Complexity_economics"));
        ni.shortestPath("/wiki/Simplicity", "/wiki/Complexity_economics");
        System.out.println(ni.influence("/wiki/Simplicity"));
        ArrayList<String> a1 = new ArrayList<>();
        a1.add("/wiki/Simplicity");
        System.out.println(ni.influence(a1));
        ArrayList<String> degreeGreedy = ni.mostInfluentialDegree(5);
        for(String s: degreeGreedy){
            System.out.println(s);
        }

    }
}
