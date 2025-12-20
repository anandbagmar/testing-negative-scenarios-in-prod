package com.eot.utilities;

import com.applitools.eyes.selenium.Eyes;
import com.eot.exceptions.TestExecutionException;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.safari.SafariDriver;
import org.openqa.selenium.safari.SafariOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;

public class Driver {
    private Driver() {
    }

    public static WebDriver create() {
        String browser = (null == System.getenv("BROWSER")) ? "chrome" : System.getenv("BROWSER");
        return createDriverFor(Browser.valueOf(browser));
    }

    public static WebDriver createDriverFor(Browser browser) {
        return createDriverFor(browser, false);
    }

    public static WebDriver createDriverFor(Browser browser, boolean useTunnel) {
        System.out.println("Running test with browser - " + browser);
        switch (browser) {
            case CHROME -> {
                return Driver.createChromeDriver();
            }
            case CHROME_HEADLESS -> {
                return Driver.createHeadlessChromeDriver();
            }
            case FIREFOX -> {
                return Driver.createFirefoxDriver();
            }
            case SAFARI -> {
                return Driver.createSafariDriver();
            }
            case EDGE -> {
                return Driver.createEdgeDriver();
            }
        }
        throw new RuntimeException(browser + " is not yet supported");
    }

    public static WebDriver createSelfHealingDriver(boolean useTunnel, String applitoolsApiKey) {
        System.out.println("Running test on Applitools Execution Cloud with Self Healing");
        return Driver.createExecutionCloudRemoteDriver(useTunnel, applitoolsApiKey);
    }

    private static WebDriver createEdgeDriver() {
        EdgeOptions options = new EdgeOptions();
        return new EdgeDriver(options);
    }

    private static WebDriver createExecutionCloudRemoteDriver(boolean useTunnel, String applitoolsApiKey) {
        WebDriver innerDriver;
        ChromeOptions chromeOptions = new ChromeOptions();
        DesiredCapabilities capabilities = new DesiredCapabilities(chromeOptions);
        capabilities.setCapability("applitools:apiKey", applitoolsApiKey);
        if (useTunnel) {
            System.out.println("Using Applitools Tunnel");
            capabilities.setCapability("applitools:tunnel", true);
        }

        String executionCloudURL = Eyes.getExecutionCloudURL();
        try {
            innerDriver = new RemoteWebDriver(new URI(executionCloudURL).toURL(), capabilities);
        } catch (MalformedURLException | URISyntaxException e) {
            throw new TestExecutionException("Error creating a new RemoteWebDriver for url: " + executionCloudURL, e);
        }
        return innerDriver;
    }

    private static WebDriver createChromeDriver() {
        return createChromeDriver(false);
    }

    private static ChromeDriver createChromeDriver(boolean isHeadless) {
        ChromeOptions options = new ChromeOptions();
        options.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});
        options.addArguments("--remote-allow-origins=*");
        if (isHeadless) {
            options.addArguments("--headless=new");
        }
        System.out.println("Chrome Options: " + options);
        ChromeDriver chromeDriver = new ChromeDriver(options);
        chromeDriver.manage().window().maximize();
        chromeDriver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
        return chromeDriver;
    }

    private static WebDriver createHeadlessChromeDriver() {
        return createChromeDriver(true);
    }

    private static WebDriver createFirefoxDriver() {
        FirefoxOptions options = new FirefoxOptions();
        options.setAcceptInsecureCerts(true);
        System.out.println("Firefox Options: " + options);
        return new FirefoxDriver(options);
    }

    private static WebDriver createSafariDriver() {
        SafariOptions options = new SafariOptions();
        return new SafariDriver(options);
    }

    public static boolean waitAndCheckIfElementIsPresent(WebDriver driver, By elementLocator) {
        try {
            waitForElementToBeVisible(driver, elementLocator);
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }

    public static boolean isElementPresent(WebDriver driver, By elementLocator) {
        try {
            driver.findElement(elementLocator);
            return true;
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    public static WebElement waitForElementToBeVisible(WebDriver driver, By elementLocator) {
        long numberOfSecondsToWait = 5;
        try {
            return (new WebDriverWait(driver, Duration.ofSeconds(numberOfSecondsToWait)).until(ExpectedConditions.visibilityOfElementLocated(elementLocator)));
        } catch (TimeoutException e) {
            savePageSourceToLogDir(driver);
            throw e;
        }
    }

    public static WebElement waitForElementToBePresent(WebDriver driver, By elementLocator) {
        long numberOfSecondsToWait = 5;
        try {
            return (new WebDriverWait(driver, Duration.ofSeconds(numberOfSecondsToWait)).until(ExpectedConditions.presenceOfElementLocated(elementLocator)));
        } catch (TimeoutException e) {
            savePageSourceToLogDir(driver);
            throw e;
        }
    }

    public static WebElement scrollIntoView(WebDriver driver, By elementLocator) {
        WebElement webElement = waitForElementToBePresent(driver, elementLocator);
        return scrollIntoView(driver, webElement);
    }

    public static WebElement scrollIntoView(WebDriver driver, WebElement webElement) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", webElement);
        return webElement;
    }

    private static void savePageSourceToLogDir(WebDriver driver) {
        try {
            String filePath = System.getenv("LOG_DIR") + "/dom.txt";
            Path path = Paths.get(filePath);
            Files.writeString(path, driver.getPageSource());
            System.out.printf("Page source saved to file: '%s' successfully!%n", filePath);
        } catch (IOException io) {
            System.out.println("An error occurred while saving the string to the file: " + io.getMessage());
            io.printStackTrace();
        }
    }

}
