# Crude Makefile for EmailScraper project. There are more advanced build                                     
# tools out there, but this basic one is likely to be installed more places.
# Requires wget to fetch the jars.
CLASSPATH=.:jsoup-1.10.2.jar:junit-4.12.jar:hamcrest-core-1.3.jar

%.class : %.java
	javac -cp $(CLASSPATH) $<

test : jars classes
	java -cp $(CLASSPATH) org.junit.runner.JUnitCore EmailScraperTest

hamcrest-core-1.3.jar :
	wget -O hamcrest-core-1.3.jar "http://search.maven.org/remotecontent?filepath=org/hamcrest/hamcrest-core/1.3/hamcrest-core-1.3.jar"

junit-4.12.jar :
	wget "https://github.com/junit-team/junit4/releases/download/r4.12/junit-4.12.jar"

jsoup-1.10.2.jar :
	wget "https://jsoup.org/packages/jsoup-1.10.2.jar"

jars: hamcrest-core-1.3.jar junit-4.12.jar jsoup-1.10.2.jar

classes: EmailScraper.class EmailScraperTest.class
