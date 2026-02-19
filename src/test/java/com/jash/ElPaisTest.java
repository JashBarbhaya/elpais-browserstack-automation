package com.jash;

import org.openqa.selenium.*;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.*;

import java.time.Duration;
import java.util.*;

public class ElPaisTest {

    WebDriver driver;
    WebDriverWait wait;

    @BeforeMethod
    @Parameters({ "os", "osVersion", "browserName", "browserVersion", "deviceName", "realMobile" })
    public void setup(String os, String osVersion, String browserName,
            String browserVersion, String deviceName,
            String realMobile) throws Exception {

        String username = System.getenv("BROWSERSTACK_USERNAME");
        String accessKey = System.getenv("BROWSERSTACK_ACCESS_KEY");

        MutableCapabilities capabilities = new MutableCapabilities();
        MutableCapabilities bstackOptions = new MutableCapabilities();

        if (realMobile.equalsIgnoreCase("true")) {

            capabilities.setCapability("platformName", os);
            bstackOptions.setCapability("deviceName", deviceName);
            bstackOptions.setCapability("realMobile", true);

        } else {

            capabilities.setCapability("browserName", browserName);
            capabilities.setCapability("browserVersion", browserVersion);
            bstackOptions.setCapability("os", os);
            bstackOptions.setCapability("osVersion", osVersion);
        }

        bstackOptions.setCapability("projectName", "ElPais Automation");
        bstackOptions.setCapability("buildName", "Parallel Build 1");
        bstackOptions.setCapability("sessionName", "Cross Browser Test");

        capabilities.setCapability("bstack:options", bstackOptions);

        driver = new RemoteWebDriver(
                new java.net.URL("https://" + username + ":" + accessKey +
                        "@hub-cloud.browserstack.com/wd/hub"),
                capabilities);

        wait = new WebDriverWait(driver, Duration.ofSeconds(20));
    }

    @Test
    public void scrapeOpinionSection() {

        driver.get("https://elpais.com/opinion/");

        // Accept cookies if present
        try {
            WebElement acceptBtn = wait.until(
                    ExpectedConditions.elementToBeClickable(
                            By.xpath("//button[contains(.,'Aceptar')]")));
            acceptBtn.click();
        } catch (Exception ignored) {
        }

        // Wait for articles to load (SAFE CONDITION)
        wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(
                By.cssSelector("article h2 a")));

        List<WebElement> articleElements = driver.findElements(By.cssSelector("article h2 a"));

        System.out.println("Total articles found: " + articleElements.size());

        List<String> articleLinks = new ArrayList<>();
        List<String> spanishTitles = new ArrayList<>();

        int limit = Math.min(5, articleElements.size());

        // Collect links and titles safely
        for (int i = 0; i < limit; i++) {

            WebElement element = articleElements.get(i);

            String link = element.getAttribute("href");
            String title = element.getText();

            if (link != null && !link.isEmpty()) {
                articleLinks.add(link);
                spanishTitles.add(title);
            }
        }

        // Visit each article
        for (int i = 0; i < articleLinks.size(); i++) {

            System.out.println("\n=====================================");
            System.out.println("Article " + (i + 1));
            System.out.println("TITLE: " + spanishTitles.get(i));

            driver.get(articleLinks.get(i));

            wait.until(ExpectedConditions.or(
                    ExpectedConditions.presenceOfElementLocated(By.tagName("article")),
                    ExpectedConditions.presenceOfElementLocated(By.tagName("main"))));

            // Download image
            try {
                WebElement image = driver.findElement(By.cssSelector("figure img"));
                String imageUrl = image.getAttribute("src");

                if (imageUrl != null && !imageUrl.isEmpty()) {
                    downloadImage(imageUrl, "article_" + (i + 1) + ".jpg");
                }
            } catch (Exception ignored) {
            }

            WebElement container;

            try {
                container = driver.findElement(By.tagName("article"));
            } catch (Exception e) {
                container = driver.findElement(By.tagName("main"));
            }

            String articleText = container.getText();

            System.out.println("CONTENT (first 800 characters):");

            if (!articleText.isEmpty()) {
                System.out.println(
                        articleText.substring(0,
                                Math.min(800, articleText.length())));
            } else {
                System.out.println("No content extracted.");
            }
        }

        // ================= TRANSLATION =================

        System.out.println("\n================ TRANSLATED TITLES ================");

        List<String> translatedTitles = new ArrayList<>();

        for (String title : spanishTitles) {

            if (title == null || title.trim().isEmpty())
                continue;

            String translated = translateToEnglish(title);
            translatedTitles.add(translated);

            System.out.println("Original: " + title);
            System.out.println("Translated: " + translated);
            System.out.println("----------------------------------");
        }

        // ================= WORD FREQUENCY =================

        System.out.println("\n================ REPEATED WORDS (>2 TIMES) ================");

        Map<String, Integer> wordCount = new HashMap<>();

        for (String title : translatedTitles) {

            String cleaned = title.toLowerCase()
                    .replaceAll("[^a-z ]", " ");

            String[] words = cleaned.split("\\s+");

            for (String word : words) {
                if (word.length() > 2) {
                    wordCount.put(word,
                            wordCount.getOrDefault(word, 0) + 1);
                }
            }
        }

        boolean found = false;

        for (Map.Entry<String, Integer> entry : wordCount.entrySet()) {
            if (entry.getValue() > 2) {
                found = true;
                System.out.println(entry.getKey() + " -> " + entry.getValue());
            }
        }

        if (!found) {
            System.out.println("No words repeated more than twice.");
        }
    }

    private void downloadImage(String imageUrl, String fileName) {
        try (java.io.InputStream in = new java.net.URL(imageUrl).openStream()) {
            java.nio.file.Files.copy(
                    in,
                    java.nio.file.Paths.get(fileName),
                    java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            System.out.println("Image downloaded: " + fileName);
        } catch (Exception ignored) {
        }
    }

    private String translateToEnglish(String text) {

        try {
            String encodedText = java.net.URLEncoder.encode(text, "UTF-8");

            String response = io.restassured.RestAssured.given()
                    .get("https://api.mymemory.translated.net/get?q="
                            + encodedText + "&langpair=es|en")
                    .then()
                    .extract()
                    .asString();

            org.json.JSONObject json = new org.json.JSONObject(response);

            String translated = json.getJSONObject("responseData")
                    .getString("translatedText");

            // Properly decode URL encoding
            translated = java.net.URLDecoder.decode(translated, "UTF-8");

            // Replace '+' with space manually
            translated = translated.replace("+", " ");

            return translated.trim();

        } catch (Exception e) {
            return text;
        }
    }

    @AfterMethod
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
