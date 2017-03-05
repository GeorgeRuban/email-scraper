# email-scraper
Java program to list email addresses from input domains, coding challenge.

Unfortunately, it's not self contained, since the requirement that the emails be in "visible" text
meant that I should use a real HTML parser - otherwise I would have just used a regexp, and only
stuck to the basic Java libraries. (There is a javax.swing.text.html.HTMLEditorKit, but it's clumsy.)
So I made it depend on JSoup, which seems to be the most popular open source HTML parser nowadays.
And while at it, I added JUnit tests.

To build it, "make". That should fetch 3 jar files (JSoup, JUnit, and Hamcrest), build the single Java
class, EmailScraper.class, and, while at it, build and run EmailScraperTest, which should pass.

To run it, "java -cp .:jsoup-1.10.2.jar EmailScraper www.jana.com web.mit.edu"
Note 
* you can specify as many domains on the same line as you like
* that the requirement not to scrape subdomains means specifying "jana.com" won't work as most links there are to "www.jana.com", which is a different subdomain
* the web.mit.edu parse will stop at 100 pages by default; if you want a different number, put it straight on the command line before the domains, so: 
     "java -cp .:jsoup-1.10.2.jar EmailScraper 25 web.mit.edu"
(I'm not using a real command line parser, as it's a toy program, but I do know where to find several.)

Here is what I get on my box when I run it as above:

Found these email addresses on the first 100 pages from www.jana.com:
press@jana.com
sales@jana.com
info@jana.com
Found these email addresses on the first 100 pages from web.mit.edu:
wjgjr@mit.edu
whbaumga@mit.edu
dwhyatt@mit.edu
9607302136.aa19971@ua.mit.edu
dunnage@mit.edu
9605132222.aa11695@ua.mit.edu
trina@mit.edu
klund@mit.edu
rslight@mit.edu
asa-elect@mit.edu
asa-exec@mit.edu
dwyatt@mit.edu
cyzh@photon.mit.edu
asa-president@mit.edu
admissions@mit.edu
asa@mit.edu
tele-info@mit.edu
9604292145.aa11798@ua.mit.edu
campus-map@mit.edu
gradadmissions@mit.edu
funds@mit.edu
jhbrown@mit.edu
asa-space@mit.edu
asa-secretary@mit.edu
kimmi@mit.edu
asa-execboard@mit.edu
web-query@mit.edu
refahrme@mit.edu
imapilot@mit.edu
search-comments@mit.edu
bale@mit.edu
asa-treasurer@mit.edu
