import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

/**
 * Created by ethantw on 4/18/2018.
 */
public class WikiPageParser {
    static final String BASE_URL = "https://en.wikipedia.org";
    //the url without the en.wikipedia.org part
    private String seedURL;
    //An integer max representing Maximum number pages to be crawled.
    private ArrayList<String> Topics;
    //list of all links on this page
    private Set<String> linksOnPage;
    //boolean for whether or not this page contains the topic keywords
    private boolean containsTopics;
    //string that contains the relevant html for the page
    private String pageHTML;


    public WikiPageParser(String seedURL, ArrayList<String> Topics){
        this.seedURL = seedURL;
        this.Topics = Topics;
        try {
            this.pageHTML = createStringFromWebPage(this.seedURL);
        }catch (IOException e){

        }
        if(!(this.pageHTML.equals("PAGE NOT FOUND"))){
            containsTopics = checkForTopics(this.pageHTML);
        }

    }

    public boolean containsAllTopics(){
        return this.containsTopics;
    }

    public Set<String> returnAllLinks(){
        this.linksOnPage = getAllLinks(pageHTML);
        return this.linksOnPage;
    }

    public String returnURL(){
        return this.seedURL;
    }

    private boolean checkForTopics(String page){
        for(int i = 0; i < Topics.size(); i++){
            if(page.contains(Topics.get(i))){
                continue;
            }else{return false;}
        }
        return true;
    }

    private Set<String> getAllLinks(String page){
        Set<String> linksOnPage = new HashSet<>();
        int index = page.indexOf("<a href=\"");
        while(index >= 0) {
            String tempLink = "";
            int i = index + 9;
            if(page.charAt(i) == '#'){
                //this is not a page we want

            }else {
                while (page.charAt(i) != '\"') {
                    tempLink += page.charAt(i);
                    i++;
                }
            }
            index = page.indexOf("<a href=\"", index+1);
            if(tempLink.length() > 6 && tempLink.startsWith("/wiki/") && !tempLink.contains(":")){
                linksOnPage.add(tempLink);
                //System.out.println("Added: " + tempLink);
            }
        }
        return linksOnPage;
    }

    private String createStringFromWebPage(String requestURL) throws IOException {
        String fileText = "";
        try (Scanner scanner = new Scanner(new URL(BASE_URL + requestURL).openStream(),
                StandardCharsets.UTF_8.toString()))
        {
            scanner.useDelimiter("\\A");
            fileText = scanner.hasNext() ? scanner.next() : "";
        }catch(IOException e){
            fileText = "PAGE NOT FOUND";
        }
        //System.out.println("String: " + fileText);
        int firstIndex = fileText.indexOf("<p>");
        int secondIndex = fileText.length();
        return fileText.substring(firstIndex, secondIndex);
    }
}
