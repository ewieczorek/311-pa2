import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {
	// write your code here
        ArrayList<String> tester = new ArrayList<>();
        tester.add("Iowa State");
        tester.add("Cyclones");

        WikiCrawler w1 = new WikiCrawler("/wiki/Iowa_State_University", 100, tester, "output.txt");
        w1.crawl();
    }
}
