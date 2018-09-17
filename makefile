switch: run_tests

run_tests: compile
	java -cp ".:junit-4.12.jar:hamcrest-core-1.3.jar" org.junit.runner.JUnitCore AllTests 2>&1 | tee test-results.txt

compile: *.java swap_url
	javac -cp ".:junit-4.12.jar:hamcrest-core-1.3.jar" *.java

swap_url: WikiCrawler.java 
	sed -i -e 's,https://en.wikipedia.org,http://web.cs.iastate.edu/~pavan,g' WikiCrawler.java

clean:
	rm *.class
	rm ref/*.class
