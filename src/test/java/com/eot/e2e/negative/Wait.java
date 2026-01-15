package com.eot.e2e.negative;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class Wait {

    public static void waitFor(int durationInSec) {
        try {
            System.out.println(String.format("Sleep for %d sec", durationInSec));

            while (durationInSec > 0) {
                System.out.printf(".");
                Thread.sleep(1000); // Sleep for 1 second
                durationInSec--;
            }
            System.out.println(String.format("Done Sleep for %d sec", durationInSec));
            System.out.println();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static WebElement waitTillElementIsPresent(WebDriver driver, By elementId) {
        return waitTillElementIsPresent(driver, elementId, 10);
    }

    public static WebElement waitTillElementIsPresent(WebDriver driver, By elementId, int numberOfSecondsToWait) {
        return (new WebDriverWait(driver, Duration.ofSeconds(numberOfSecondsToWait)).until(ExpectedConditions.presenceOfElementLocated(elementId)));
    }
}
