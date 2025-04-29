# Web-Crawler

This project implements a Web-Crawler, which provides a compact overview of the given website and linked websites by only listing the headings and the links.

## Features
- **Extract Headings and Links**: Collects all headings (`<h1>` to `<h6>`) and links from the input URL
- **Recursive Crawling**: Visits all links up to a certain depth and filters them based on specified domains (each website is crawled only once)
- **Broken Link Detection**: Highlights broken links
- **Markdown File**: The results is stored in a single markdown file (.md extension)

## Getting started

### Build Project


### Run the Web-Crawler
To use the Web-Crawler, te user has to enter the following in the command-line: `java MainProgram <URL> <depth> <domain1,domain2,...>`. 
It must be noted that depth has to be an integer, the url must be formatted as follows: `https://www.example.at` and you can enter one or more domains like: `weather.example.at,sport.example.at`.

### Testing

