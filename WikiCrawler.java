import java.io.*;
import java.lang.reflect.Array;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;


/**
 * Created by ethantw on 4/18/2018.
 */
public class WikiCrawler {
    //GLOBAL VARIABLES
    //A string seedUrl–relative address of the seed url (within Wiki domain).
    private String seedURL;
    //An integer max representing Maximum number pages to be crawled.
    private int max;
    //An array list of keywords called topics. The keywords describe a particular topic.
    private ArrayList<String> Topics;
    //A string fileName representing name of a ﬁle–The graph will be written to this ﬁle
    private String fileName;
    //a static, final global variable named BASE_URL with value https://en.wikipedia.org a
    static final String BASE_URL = "https://en.wikipedia.org";

    ArrayList<WikiPageParser> parserList;

    private Set<String> allURLs;
    private Set<String> visitedURLs;
    private Set<String> visitedURLsWithTopics;

    public  HashMap<String, ArrayList<String>> graph;
    /*
    Constructor:
    @param seedURL: A string seedUrl–relative address of the seed url (within Wiki domain).
    @param max: An integer max representing Maximum number pages to be crawled.
    @param Topics: An array list of keywords called topics. The keywords describe a particular topic.
    @param fileName: A string fileName representing name of a ﬁle–The graph will be written to this ﬁle
     */
    public WikiCrawler(String seedURL, int max, ArrayList<String> Topics, String fileName){
        this.seedURL = seedURL;
        this.max = max;
        this.Topics = Topics;
        this.fileName = fileName;

        this.allURLs = new HashSet<>();
        this.visitedURLs = new HashSet<>();
        this.visitedURLsWithTopics = new HashSet<>();

        this.parserList = new ArrayList<>();
        this.graph = new HashMap<>();
    }

    public void crawl() {
        allURLs.add(this.seedURL);
        ArrayList<String> pagesToVisit = new ArrayList<>();
        //make a page parser for the seed URL
        WikiPageParser wpp = new WikiPageParser(this.seedURL, Topics);
        visitedURLs.add(this.seedURL);
        parserList.add(wpp);
        if(wpp.containsAllTopics()){
            visitedURLsWithTopics.add(this.seedURL);
            Set<String> temp = wpp.returnAllLinks();
            int amountPassed = 0;
            for (String URL: temp) {
                if(visitedURLsWithTopics.size() < max) {
                    //System.out.println("checking URL: " + URL);
                    if(!allURLs.contains(URL)) {
                        allURLs.add(URL);
                        WikiPageParser tempParser = new WikiPageParser(URL, Topics);
                        visitedURLs.add(URL);
                        if (tempParser.containsAllTopics()) {
                            visitedURLsWithTopics.add(URL);
                            //if it was already in the set do nothing
                            //else{
                            parserList.add(tempParser);
                            //}

                            System.out.println(URL + " Contains all of the keywords");

                        }
                    }
                }
                amountPassed++;
                if(amountPassed % 25 == 0){
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        //e.goFuckYourself
                    }
                }
            }
            for(WikiPageParser URLs: parserList){
                //go through everything in it's Set of links and check if it exists in visitedURLsWithTopics
                System.out.println("making the graph");
                ArrayList<String> edges = new ArrayList<>();
                for(String links: URLs.returnAllLinks()){
                    if(visitedURLsWithTopics.contains(links)){
                        edges.add(links);
                    }
                }
                this.graph.put(URLs.returnURL(), edges);
                System.out.println(URLs.returnURL() + " Contains the edges: ");
                for(String edge: edges){
                    System.out.print(edge + ", ");
                }
            }
        }
        printTofile();
            //read through the document to find the first instance of "<p>"
            //find any instances of "<a href="" and look at the link
            //only include links that start with "/wiki/"
            //don't include any links that start with "#" or contains ":"
            //check all links for instances of the keywords in the Topics variable

    }

    private void printTofile(){
        //this function writes the data in data to a new line
        //in the text file denoted by fileName. If fileName doesn't
        //exist it makes the file

        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
            writer.write(visitedURLsWithTopics.size() + "\n");
            for(String list: visitedURLsWithTopics){
                for(String str: graph.get(list)){
                    writer.write(list + " " + str + "\n");
                }
            }
            writer.close();
        } catch (IOException e) {
            System.out.println("Couldn't open file");
        }
    }
}
