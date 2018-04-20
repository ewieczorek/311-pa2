import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {
	// write your code here
        ArrayList<String> tester = new ArrayList<>();
        //tester.add("Iowa State");
        //tester.add("Cyclones");

        WikiCrawler w1 = new WikiCrawler("/wiki/Computer_Science", 100, tester, "output.txt");
        w1.crawl();

        NetworkInfluence ni = new NetworkInfluence("wikiCC.txt");
        System.out.println(ni.distance("/wiki/Complex_networks", "/wiki/Warren_Weaver"));
    }
}
