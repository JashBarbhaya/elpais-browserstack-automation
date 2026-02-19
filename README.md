# El País Opinion Scraper – Selenium + BrowserStack

## Project Overview

This project demonstrates web scraping, API integration, text processing, and cross-browser testing using Selenium and TestNG.
The automation script extracts articles from the **Opinion** section of *El País* (Spanish news website), translates article titles to English, performs word frequency analysis, and executes tests locally as well as on BrowserStack across multiple browsers and devices.

---

##  Features Implemented

### 1. Web Scraping (Spanish Content)

* Navigates to: [https://elpais.com/opinion/](https://elpais.com/opinion/)
* Ensures the website content is displayed in Spanish
* Extracts the **first five articles**
* Prints:

  * Article title (Spanish)
  * Article content (first 800 characters)
* Downloads and saves the **cover image** (if available)

### 2️. Translation API Integration

* Uses RapidApi Translation 
* Translates article titles from Spanish → English
* Prints translated titles

### 3️. Text Processing

* Analyzes translated titles
* Identifies words repeated more than twice
* Prints repeated words with occurrence count

### 4️. Cross-Browser Testing

* Executed locally (Chrome)
* Executed on **BrowserStack**
* Runs in parallel across 5 environments:

  * Windows + Chrome
  * Windows + Edge
  * macOS + Safari
  * Android (Samsung device)
  * iOS (iPhone device)

---

## 🛠 Tech Stack

* Java 17
* Selenium 4
* TestNG
* Maven
* RestAssured (API calls)
* BrowserStack (Cloud execution)

---

## 📂 Project Structure

```
elpais-automation
│── src/test/java/com/jash/ElPaisTest.java
│── testng.xml
│── pom.xml
│── README.md
```

---

## ⚙️ Setup Instructions

### 1️. Clone Repository

```
git clone https://github.com/<your-username>/elpais-automation.git
cd elpais-automation
```

### 2️. Set BrowserStack Credentials

Set environment variables:

**Windows (PowerShell):**

```
setx BROWSERSTACK_USERNAME "your_username"
setx BROWSERSTACK_ACCESS_KEY "your_access_key"
setx RAPIDAPI_KEY "your_api_key_here"

```

**Mac/Linux:**

```
export BROWSERSTACK_USERNAME=your_username
export BROWSERSTACK_ACCESS_KEY=your_access_key
setx RAPIDAPI_KEY "your_api_key_here"
```

Restart terminal after setting variables.

---

## ▶️ Run Tests

### Run Locally

```
mvn clean test
```

### Run on BrowserStack (Parallel Execution)

```
mvn clean test
```

(TestNG parallel configuration is defined in `testng.xml`)

---

## 📊 Expected Output

* Spanish article titles and content printed
* Images saved as:

  * article_1.jpg
  * article_2.jpg
  * ...
* English translated titles
* Repeated word analysis
* Successful execution across 5 browser/device combinations

---

## ✅ Assignment Coverage

✔ Web scraping using Selenium
✔ Spanish content validation
✔ Image download automation
✔ Translation API integration
✔ Text processing & frequency analysis
✔ Parallel cross-browser execution on BrowserStack

---

## 📌 Notes

* Script includes explicit waits for stability across desktop and mobile devices.
* Designed for reliability in cloud execution environments.

---

## 👤 Author

**Jash Barbhaya**
B.Tech Computer Engineering
