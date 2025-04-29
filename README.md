# Web-Crawler

This project implements a Web-Crawler, which provides a compact overview of the given website and linked websites by only listing the headings and the links.

## Features
- **Extract Headings and Links**: Collects all headings (`<h1>` to `<h6>`) and links from the input URL
- **Recursive Crawling**: Visits all links up to a certain depth and filters them based on specified domains (each website is crawled only once)
- **Broken Link Detection**: Highlights broken links
- **Markdown File**: The results is stored in a single markdown file (.md extension)


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
