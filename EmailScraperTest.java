import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


/**                                                                                                          
 * JUnit tests for EmailScraper.                                                                             
 */
public class EmailScraperTest {
  EmailScraper scraper;

  @Before
  public void setUp() {
    scraper = new EmailScraper("my.domain.com", 10);
  }

  @Test
  public void testAddEmails() {
    String html = "<head><title>Some text with emails like a@b.org</title></head>" +
        "<!--- hidden@email.com --> Visible: foo@bar.com, me-too@elsewhere.co.uk " +
        " a mixed case copy of Foo@Bar.com, and 1234567@long.topleveldomain.";
    Document doc = Jsoup.parse(html);

    scraper.addEmails(doc);
    assertEquals(4, scraper.getEmails().size());
    assertTrue(scraper.getEmails().contains("a@b.org"));
    assertTrue(scraper.getEmails().contains("foo@bar.com"));
    assertTrue(scraper.getEmails().contains("me-too@elsewhere.co.uk"));
    assertTrue(scraper.getEmails().contains("1234567@long.topleveldomain"));
  }

  @Test
  public void testAddLinks() {
    String html = "<a href=http://my.domain.com/page></a> " +
        "<a href=https://my.domain.com/secure></a> " +
        "<a href=http://my.domain.com/visited></a> " +
        "<a href=http://differentsub.domain.com/></a> " +
        "<a href=http://my.domain.com/duplicate></a> " +
        "<a href=http://somewhere.else.com/></a> " +
        "<!-- <a href=http://my.domain.com/commentedout/></a> -->";
    Document doc = Jsoup.parse(html);
    scraper.visitedPages.add("http://my.domain.com/visited");

    scraper.addLinks(doc);
    assertEquals(3, scraper.futurePages.size());
    assertTrue(scraper.futurePages.contains("http://my.domain.com/page"));
    assertTrue(scraper.futurePages.contains("https://my.domain.com/secure"));
    assertTrue(scraper.futurePages.contains("http://my.domain.com/duplicate"));
  }
}
