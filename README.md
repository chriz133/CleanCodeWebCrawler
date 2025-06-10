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
│           ├── adapter/          # Adapters for third-party libraries (e.g., JsoupHtmlParser, JsoupWebDocument)
│           ├── core/             # Core application logic (e.g., ConcurrentWebCrawler)
│           ├── interfaces/       # Interfaces (e.g., Crawler, HtmlParser, WebDocument)
│           ├── model/            # Data models (e.g., Website, Heading)
│           ├── utilities/        # Utility classes (e.g., UrlUtils)
│           └── main/             # Entry point (e.g., WebCrawler class with main method)
└── test/
    └── java/
        └── at/aau/crawler/
            ├── adapter/          # Unit tests for adapter classes (e.g., JsoupHtmlParserTest)
            ├── core/             # Unit tests for core logic (e.g., ConcurrentWebCrawlerTest)
            ├── model/            # Unit tests for models (optional)
            ├── interfaces/       # Tests or mocks for interfaces (optional, rarely needed)
            └── utilities/        # Unit tests for utilities (e.g., UrlUtilsTest)
```

---

## Markdown Output

The Markdown output represents the structure of a website and its subpages. It follows these rules:

---

### 1. Indentation by Depth

Each page is indented according to its **crawl depth**:

- `Depth: 1` → no indentation
- `Depth: 2` → indented one level (tab or 4 spaces)
- `Depth: 3` → indented two levels
- etc.

This makes the **link structure** of the site visually clear.

---

### 2. Links Are Shown Only at First Occurrence

A new Website of a link (`Link --> https://...`) is **only displayed where it first appears**. If the same URL is encountered later, it is **not shown again**, unless it is discovered at a deeper depth for the first time.

This keeps the output clean and avoids duplication and infinite loops.

---

### 3. Hierarchy via `Ownurl` and `Parenturl`

Each page includes:

- `Ownurl`: the URL of the current page
- `Parenturl`: (only for depth > 1) the URL of the referring page

This allows tracing **how the page was reached** and represents the crawl hierarchy.

---

### 4. Headings as Content Summary

The page content is summarized using extracted HTML heading tags:

```html
<h1>...</h1>
<h2>...</h2>
<h3>...</h3>
```

This provides a quick overview of the **editorial content** of the page.

---

## Example (simplified)

```markdown
Ownurl: https://www.orf.at
Depth: 1
<h1>Main Headline</h1>
<h2>Categories</h2>
<h3>Other Topics</h3>

Link --> https://wetter.orf.at

    Ownurl: https://wetter.orf.at
    Parenturl: https://www.orf.at
    Depth: 2
    <h1>Weather Overview</h1>
    <h2>Region Selection</h2>
```

---
## Summary

```markdown
- Each page is shown with its URL (`Ownurl`) and crawl depth (`Depth`).
- HTML headings (`<h1>` to `<h3>`) are extracted and displayed.
- Each link (`Link --> ...`) appears only once, where it was first discovered.
- Repeated or known URLs are not listed again.
- Deeper levels are indented to preserve structure.
```

---

## How to Build and Run

### 1. Clone the Repository

```bash
git clone https://github.com/chriz133/CleanCodeWebCrawler.git
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
