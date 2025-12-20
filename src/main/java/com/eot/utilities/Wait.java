package com.eot.utilities;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.Calendar;

public class Wait {
    private Wait() {
    }

    public static void waitFor(int durationInSec) {
        try {
            System.out.println("\n\t" + Calendar.getInstance().getTime());
            System.out.println(String.format("\tSleep for %d sec",
                                             durationInSec));
            Thread.sleep(durationInSec * 1000);
            System.out.println("\t" + Calendar.getInstance().getTime() + "\n");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static WebElement waitTillElementIsPresent(WebDriver driver, By elementId) {
        return waitTillElementIsPresent(driver, elementId, 10);
    }

    public static WebElement waitTillElementIsPresent(WebDriver driver, By elementId, int numberOfSecondsToWait) {
        try {
            return (new WebDriverWait(driver, Duration.ofSeconds(numberOfSecondsToWait)).until(ExpectedConditions.presenceOfElementLocated(elementId)));
        } catch (NoSuchElementException e) {
            System.out.println("Element '" + elementId.toString() + "' not present");
            System.out.println(driver.getPageSource());  // This prints the full HTML of the current page
            throw e;
        }
    }

    public static WebElement waitTillElementIsVisible(WebDriver driver, By elementId) {
        return waitTillElementIsVisible(driver, elementId, 10);
    }

    public static WebElement waitTillElementIsVisible(WebDriver driver, By elementId, int numberOfSecondsToWait) {
        try {
            return (new WebDriverWait(driver, Duration.ofSeconds(numberOfSecondsToWait)).until(ExpectedConditions.visibilityOfElementLocated(elementId)));
        } catch (NoSuchElementException e) {
            System.out.println("Element '" + elementId.toString() + "' not visible");
            System.out.println(driver.getPageSource());  // This prints the full HTML of the current page
            throw e;
        }
    }
    public static WebElement waitTillElementIsClickable(WebDriver driver, By elementId) {
        return waitTillElementIsClickable(driver, elementId, 10);
    }

    public static WebElement waitTillElementIsClickable(WebDriver driver, By elementId, int numberOfSecondsToWait) {
        try {
            return (new WebDriverWait(driver, Duration.ofSeconds(numberOfSecondsToWait)).until(ExpectedConditions.elementToBeClickable(elementId)));
        } catch (NoSuchElementException e) {
            System.out.println("Element '" + elementId.toString() + "' not visible");
            System.out.println(driver.getPageSource());  // This prints the full HTML of the current page
            throw e;
        }
    }

    public static WebElement scrollTillElementIntoView(WebDriver driver, By elementId) {
        try {
            WebElement element = driver.findElement(elementId);
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
            return element;
        } catch (NoSuchElementException e) {
            System.out.println("Element '" + elementId.toString() + "' not found");
            System.out.println(driver.getPageSource());  // This prints the full HTML of the current page
            throw e;
        }
    }

}
