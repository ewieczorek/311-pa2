import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Scanner;


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
    }

    public void crawl() {
        try {
            ArrayList<String> pagesToVisit = new ArrayList<>();

            /*URL url = new URL(BASE_URL + seedURL);
            InputStream is = url.openStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            ArrayList<String> pagesToVisit = new ArrayList<>();
            boolean exit = false;
            boolean activeContent = false;
            while(exit == false){
                try{
                    String line = br.readLine();
                    if(activeContent == false){
                        if (line.contains("<p>")){
                            activeContent = true;
                        }else{ continue;}
                    }
                    if(activeContent == true){
                        //If we're in the active content now it's time to start parsing links
                        int index = line.indexOf("<a href=\"");
                        while(index >= 0) {
                            String tempLink = null;
                            int i = index;
                            while(line.charAt(i) != "\""){

                            }
                            index = line.indexOf("<a href=\"", index+1);
                        }
                    }
                }catch(NullPointerException e){
                    exit = true;
                }
            }*/
            //read through the document to find the first instance of "<p>"
            //find any instances of "<a href="" and look at the link
            //only include links that start with "/" e.g. /wiki/XXX
            //don't include any links that start with "#" or contains ":"
            //check all links for instances of the keywords in the Topics variable

        } catch (IOException e) {
            System.out.println("Couldn't open the URL");
        }
    }

    private boolean checkForTopics(String page){
        for(int i = 0; i < Topics.size(); i++){
            if(page.contains(Topics.get(i))){
                continue;
            }else{return false;}
        }
        return true;
    }

    private ArrayList<String> getAllLinks(String page){
        ArrayList<String> linksOnPage = new ArrayList<>();
        int index = page.indexOf("<a href=\"");
        while(index >= 0) {
            String tempLink = "";
            int i = index + 10;
            if(page.charAt(index + 10) == '#'){
                //this is not a page we want
            }
            while(page.charAt(i) != '\"'){
                tempLink += page.charAt(i);
            }
            linksOnPage.add(tempLink);
            index = page.indexOf("<a href=\"", index+1);
        }
        return linksOnPage;
    }

    private String createStringFromWebPage(String requestURL) throws IOException{
        String fileText = "";
        try (Scanner scanner = new Scanner(new URL(requestURL).openStream(),
            StandardCharsets.UTF_8.toString()))
        {
            scanner.useDelimiter("\\A");
            fileText = scanner.hasNext() ? scanner.next() : "";
        }
        int firstIndex = fileText.indexOf("<p>");
        int secondIndex = fileText.indexOf("<span class=\"mw-headline\" id=\"Notes_and_references\">");
        return fileText.substring(firstIndex, secondIndex);
    }

    private void printTofile(String data){
        //this function writes the data in data to a new line
        //in the text file denoted by fileName. If fileName doesn't
        //exist it makes the file
    }
}
