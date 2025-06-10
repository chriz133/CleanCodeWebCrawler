# Web-Crawler

This project implements a Web-Crawler, which provides a compact overview of the given website and linked websites by only listing the headings and the links. A ThreadPoolExecutor is used to regulate how many threads are started and runned. After all threads have finished, the report is written. If any errors accure during runtime, they are also logged in the report. 
To gurantee SRP, OCP, DIP, cohesion, coupling and LoD as much as possible, the code is splitted in multiple classes and interfaces.  

## Features
- **Extract Headings and Links**: Collects all headings (`<h1>` to `<h6>`) and links from the input URL
- **Recursive Crawling**: Visits all links up to a certain depth and filters them based on specified domains (each website is crawled only once)
- **Broken Link Detection**: Highlights broken links
- **Markdown File**: The results is stored in a single markdown file (.md extension)
  
## New Features
- **Concurrent Crawling**: Same procedure as above but this time each website is processed by a separate thread.
- **Combined Results**: The results are stored in a single report that retains the original structure of the websites.
- **Error Handling**: Users are informed by logging the error message in the report, if there are any errors.
- **Boundaries to Third-Party Libraries**: Jsoup HTML parser is one dependency that is used. For this dependency a new class is implemended which abstracts needed methods of Jsoup, to fullfill the abstractation level. This enables to switch libaries if neccessary, without changing the "main" class. Furthermore, it makes it easier to handle tests.

## Project Structure

```
src/
├── main/
│   └── java/
│       └── at/aau/crawler/
│           ├── interfaces/   # Crawler and WebsiteWriter interfaces
│           ├── model/        # Data models (Website, Heading)
│           ├── services/     # WebCrawlerService class
│           ├── utilities/    # SimpleWebCrawler, MarkdownWebsiteWriter
│           └── main/         # WebCrawler (Main class with main method)
└── test/
    └── java/
        └── at/aau/crawler/
            ├── model/
            ├── services/
            └── utilities/
```

---

## How to Build and Run

### 1. Clone the Repository

```bash
git clone https://github.com/yourusername/CleanCodeWebCrawler.git
cd CleanCodeWebCrawler
```

### 2. Build the Project

Make sure Maven is installed (`mvn -v`).

```bash
mvn clean install
```

This will:
- Compile the source code
- Run all unit tests
- Package the application

### 3. Run the Application

```bash
java -cp target/CleanCodeWebCrawler-1.0-SNAPSHOT.jar at.aau.crawler.main.WebCrawler <URL> <depth> <domain1,domain2,...>
```

Example:

```bash
java -cp target/CleanCodeWebCrawler-1.0-SNAPSHOT.jar at.aau.crawler.main.WebCrawler https://example.com 2 example.com
```

---

## How to Test

Run:

```bash
mvn test
```

- Runs all unit tests automatically.
- Tests cover crawling, output writing, data models, and error handling.
- Uses **JUnit 5** and **Mockito** for test automation.

---

## Technologies Used

- **Java 17**
- **Maven** (build and dependency management)
- **JUnit 5** (testing framework)
- **Mockito** (mocking framework)
- **Jsoup** (HTML parsing)

---

## Notes

- Only URLs belonging to the specified domain(s) are crawled.
- Broken links are identified and marked in the output report.
- Duplicate visits to the same page are avoided.
- In case of invalid input arguments, the program prints an error message and exits.
