import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class GettingToPhilosophy {

    static String protocol = "https://";
    static String base = "en.wikipedia.org/";
    static String article = "wiki/";
    static String api = "w/api.php?";
    static String failure = "This page never gets to philosophy!";

    public static void main(String[] args) throws Exception {
        PhilosophyDB.connect();
        Scanner in = new Scanner(System.in);
        in.useDelimiter(System.getProperty("line.separator")); 
        String term;
        System.out.println("Input q to quit.");
        System.out.println("Input a to get analytics.");
        int clicks = 0;
        while(true) {
            System.out.println("Determine a starting point (enter a wikipedia title, url, or search term): ");
            term = in.next();
            if(term.equals("q")) {
            	in.close();
                break;
            } else if (term.equals("a")) {
            	printAnalytics();
            	continue;
            }
            // Check to see if this input has already been processed.
            if(PhilosophyDB.exists(term)) {
            	if (PhilosophyDB.get(term) == -1) {
            		System.out.println(failure);
            	} else {
            		System.out.println(PhilosophyDB.get(term));
            	}
                continue;
            }

            int resp = -1;
            try {
                URL url = new URL(protocol + base + article + term);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                resp = connection.getResponseCode();

                if(resp == 200) {
                    // term is WIKIPEDIA TITLE
                    clicks = getToPhilosophy(protocol + base + article + term);
                } else if(term.contains(base)) {
                    // term is URL
                    clicks = getToPhilosophy(term);
                } else {
                    // term is SEARCH TERM
                    String searchResult = wikiSearch(term);
                    clicks = getToPhilosophy(searchResult);
                }
                PhilosophyDB.put(term, clicks);
                if(clicks == -1) {
                	System.out.println(failure);
                } else {
                	System.out.println(clicks);
                }
                
            } catch (Exception e) {
                 System.out.println("Error fetching wikipedia page. Are you connected to the internet?");
            }
        }
        printAnalytics();
    }

    private static int getToPhilosophy(String link) throws IOException{
    	int count = 0;
    	String philoLink = "https://en.wikipedia.org/wiki/Philosophy";
		String philoLink2 = "https://en.wikipedia.org/wiki/philosophy";
    	String currentLink = link;
    	String body;
    	Document doc;
    	Elements elems;
    	String str = "";
    	HashSet<String> hash = new HashSet<String>();
    	while(!currentLink.equals(philoLink) && !currentLink.equals(philoLink2)) {
    		if(hash.contains(currentLink)) {
    			return -1;
    		}
    		hash.add(currentLink);
    		body = getHttpBody(currentLink);
        	doc = Jsoup.parse(body);
        	elems = doc.select("p > a");
        	for (Element e : elems) {
        		if (e.attr("href").contains("/wiki")) {
        			str = e.attr("href");
        			str = str.substring(1);
        			break;
        		}
        	}
        	if(str.equals("")) {
        		return -1;
        	}
        	System.out.println(str.substring(5));
        	currentLink = protocol + base + str;
        	count += 1;
    	}
        return count;
    }

    // Returns the URL of the article that most matches the search term. Returns null if could not be found or error.
    private static String wikiSearch(String term) throws IOException, JSONException{
    	String body = getHttpBody(protocol + base + api + "action=opensearch&search=" + term + "&limit=1&format=json");
    	JSONArray obj = new JSONArray(body);
        JSONArray arr = obj.getJSONArray(3);
        return arr.getString(0); 
    }
    
    private static String getHttpBody(String link) throws IOException {
    	int read_len = 100;
    	URL searchURL = new URL(link);
        HttpURLConnection connection = (HttpURLConnection) searchURL.openConnection();
        String resp = null;
        if(connection.getResponseCode() == 200) {
            InputStream body = connection.getInputStream();
            StringBuffer strbuf = new StringBuffer();
            byte[] buf = new byte[read_len];
            int count;
            while ((count = body.read( buf, 0, read_len)) != -1) {
                strbuf.append(new String(Arrays.copyOfRange(buf, 0, count)));
            }
            
            resp = strbuf.toString();
        }
        return resp;
    }
    
    private static void printAnalytics() throws Exception {
    	int total = PhilosophyDB.getAll();
    	int success = PhilosophyDB.getSuccess();
    	int fail = PhilosophyDB.getFail();
    	int sum = PhilosophyDB.getSum();
    	System.out.println("Queries that ended in philosophy: " + success + "/" + total);
    	System.out.println("Queries that did not end in philosophy: " + fail + "/" + total);
    	System.out.println("Average number of clicks to get to philosophy: " + String.format("%.2f", sum*1.0/success));
    }
}