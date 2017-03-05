import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**                                                                                                          
 * Takes an internet domain name and prints out a list of email addresses on                                 
 * that website only. The program should not crawl other subdomains (for                                     
 * example, given "jana.com", it should not crawl blog.jana.com or                                           
 * technology.jana.com).                                                                                     
 *                                                                                                           
 * Note that this means that specifying "jana.com" will NOT crawl links                                      
 * specified as www.jana.com; www is the conventional subdomain, but it                                      
 * is a different subdomain.                                                                                 
 *                                                                                                           
 * These are expected output from www.jana.com                                                               
 * Found these email addresses:                                                                              
 * sales@jana.com                                                                                            
 * press@jana.com                                                                                            
 * info@jana.com                                                                                             
 *                                                                                                           
 * https://gist.github.com/clee-jana/a1535a7ea0c7b0fed41d                                                    
 *                                                                                                           
 * The email addresses should be all human visible text that looks like an email                             
 * address, not in comments, inline JavaScript, and similar invisible text.                                  
 * The program does not obey robots.txt.                                                                     
 * The program will stop after a "reasonable" limit of pages have been parsed,                               
 * which defaults to 100, but can be configured by commandline argument                                      
 * (just pass in a single integer on the command line before the domain;                                     
 *  we can use https://commons.apache.org/proper/commons-cli/ for real command                               
 *  line parsing if we want to make this a real program). 
 *
 * @author George Ruban, george_ruban@yahoo.com
 */
public class EmailScraper {

  /** The domain/website we're scraping emails off of. */
  private String domain;

  /** Domain in http format, for avoiding subdomains. */
  private String httpDomain;

  /** Domain in https format, for avoiding subdomains. */
  private String httpsDomain;

  /** Maximum number of pages to scrape. */
  private int maxPages;

  /** All gathered emails; a set, so no duplication. */
  private Set<String> emails = new HashSet<>();

  /** Pages we've already scraped. */
  protected Set<String> visitedPages = new HashSet<>();
  
  /** Pages we've yet to scrape; a set, so no duplication. */
  protected NavigableSet<String> futurePages = new TreeSet<>();

  /**                                                                                                        
   * @param domain Domain to scrape, for example www.jana.com                                                
   * @param maxPages Maximum number of pages to scrape, -1 for infinite.                                     
   */
  public EmailScraper(String domain, int maxPages) {
    this.maxPages = maxPages;
    this.domain = domain.toLowerCase();
    this.httpDomain = "http://" + this.domain;
    this.httpsDomain = "https://" + this.domain;
  }

  /** Scrapes an entire domain for email addresses. */
  public void scrape() {
    futurePages.add(httpDomain);
    while (futurePages.size() > 0 &&
        (maxPages < 0 || visitedPages.size() < maxPages)) {
      try {
        scrapeUrl(futurePages.pollFirst());
      } catch (IOException e) {
        // Jana.com has this wonderful link: <a href="http:////www.jana.com/home"                            
        // but no doubt other errors should also not crash us.                                               
      }
    }
  }

  /** Scrapes a specific URL for email addresses and links within the domain. */
  private void scrapeUrl(String url) throws IOException {
    visitedPages.add(url);
    Document doc = Jsoup.connect(url).get();
    addLinks(doc);
    addEmails(doc);
  }

  /** 
   * Queue up all the links that match the domain and                                                       
   * haven't been traversed or queued yet.
   */
  protected void addLinks(Document doc) {
    Elements links = doc.select("a[href]");
    for (Element link : links) {
      String absHref = link.attr("abs:href").toLowerCase();
      if ((absHref.startsWith(httpDomain) ||
          absHref.startsWith(httpsDomain)) &&
          !visitedPages.contains(absHref)) {
        futurePages.add(absHref);
      }
    }
  }

  /**                                                                                                        
   * Regexp to match ... most ... email addresses.                                                           
   * From http://thedailywtf.com/articles/Validating_Email_Addresses                                         
   * Here is a possibly more complete one that I'm intentionally not using:                                  
   * https://blog.codinghorror.com/regex-use-vs-regex-abuse/                                                 
   */
  private static Pattern emailPattern = Pattern.compile(
      "[-!#$%&'*+/0-9=?A-Z^_a-z{|}~](\\.?[-!#$%&'*+/0-9=?A-Z^_a-z{|}~])*" +
      "@[a-zA-Z](-?[a-zA-Z0-9])*(\\.[a-zA-Z](-?[a-zA-Z0-9])*)+");


  /**
   * Parse out everything that looks like an email address from                                             
   * the human-visible text of the page. 
   */
  protected void addEmails(Document doc) {
    String text = doc.text();
    Matcher m = emailPattern.matcher(text);
    while(m.find()) {
      emails.add(m.group().toLowerCase());
    }
  }

  public Set<String> getEmails() {
    return emails;
  }
  
  /**                                                                                                        
   * Command line arguments can be domains (jana.com) or integers                                            
   * that affect the maximum number of pages to get from subsequent                                          
   * domains.                                                                                                
   */
  public static void main(String[] args) throws IOException {
    int maxPages = 100;
    for (String arg : args) {
      // Crude way of reading command line: if it's an integer, use it.                                      
      try {
        maxPages = Integer.parseInt(arg);
        continue;
      } catch(NumberFormatException e) {}

      // Actual invocation.                                                                                  
      EmailScraper scraper = new EmailScraper(arg, maxPages);
      scraper.scrape();
      System.out.println("Found these email addresses on the first " + maxPages +
          " pages from "  + arg + ":");
      for (String email : scraper.getEmails()) {
        System.out.println(email);
      }
    }
  }
}
