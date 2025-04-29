# Web-Crawler

This project implements a Web-Crawler, which provides a compact overview of the given website and linked websites by only listing the headings and the links.

## Features
- **Extract Headings and Links**: Collects all headings (`<h1>` to `<h6>`) and links from the input URL
- **Recursive Crawling**: Visits all links up to a certain depth and filters them based on specified domains (each website is crawled only once)
- **Broken Link Detection**: Highlights broken links
- **Markdown File**: The results is stored in a single markdown file (.md extension)

## Getting started

### Build Project
- **Clone Repository**: use git clone <URL>
- **Build the Project with Maven**: `mvn clean package` Maven will compile the source code, run tests, and package the application into a JAR-file. The output is placed in the `target` directory. It should look like: `target/WebCrawler-1.0-SNAPSHOT.jar`

### Run the Web-Crawler
After the succesful build the application can be runned with the generated JAR-file.
To use the Web-Crawler, te user has to enter the following in the command-line: `java -jar target/WebCrawler-1.0-SNAPSHOT.jar <URL> <depth> <domain1,domain2,...>`. 
It must be noted that depth has to be an integer, the url must be formatted as follows: `https://www.example.at` and you can enter one or more domains like: `weather.example.at,sport.example.at`.

### Testing
To test the Project the user first of all has to download `Maven 3.9.9`. Now the test can be runned with `mvn test`.
