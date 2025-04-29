# Web-Crawler

This project implements a Web-Crawler, which provides a compact overview of the given website and linked websites by only listing the headings and the links.

## Features
- **Extract Headings and Links**: Collects all headings (`<h1>` to `<h6>`) and links from the input URL
- **Recursive Crawling**: Visits all links up to a certain depth and filters them based on specified domains (each website is crawled only once)
- **Broken Link Detection**: Highlights broken links
- **Markdown File**: The results is stored in a single markdown file (.md extension)

## Getting started
