package com.eot.e2e.screens;

import com.eot.e2e.screens.android.TimedOutScreenAndroid;
import com.eot.e2e.screens.web.TimedOutScreenWeb;
import com.znsio.teswiz.context.TestExecutionContext;
import com.znsio.teswiz.entities.Platform;
import com.znsio.teswiz.runner.Driver;
import com.znsio.teswiz.runner.Drivers;
import com.znsio.teswiz.runner.Runner;
import com.znsio.teswiz.runner.Visual;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public abstract class TimedOutScreen {
    private static final String SCREEN_NAME = TimedOutScreen.class.getSimpleName();
    private static final Logger LOGGER = LogManager.getLogger(SCREEN_NAME);
    private static Driver driver;
    private static Visual visually;
    private static TestExecutionContext context;
    private static final By BY_SOMETHING_WENT_WRONG_MESSAGE_XPATH = By.xpath("//div[contains(@class,\"jdsErrorScreen_containerLayout_\")]");
    private static final By BY_INTERNATIONAL_ROAMING_PLANS_HEADING_XPATH = By.xpath("//a[@aria-label=\"International Services\"]");

    public static TimedOutScreen get() {
        Driver driver = Drivers.getDriverForCurrentUser(Thread.currentThread().getId());
        Platform platform = Runner.fetchPlatform(Thread.currentThread().getId());
        LOGGER.info(SCREEN_NAME + ": Driver type: " + driver.getType() + ": Platform: " + platform);
        visually = Drivers.getVisualDriverForCurrentUser(Thread.currentThread().getId());
        long threadId = Thread.currentThread().getId();
        context = Runner.getTestExecutionContext(threadId);

        switch (platform) {
            case web:
                return new TimedOutScreenWeb(driver, visually);
            case android:
                return new TimedOutScreenAndroid(driver, visually);
        }
        throw new NotImplementedException(
                SCREEN_NAME + " is not implemented in " + Runner.getPlatform());
    }

    public String getSomethingWentWrongMessage() {
        LOGGER.info("Getting 'Something went wrong' message from Timed Out Screen");
        visually.checkWindow(SCREEN_NAME, "Timed Out Screen");
        String errorMessage = waitForTimeoutScreen().getText().replace("\n", " ");
        LOGGER.info("'Something went wrong' message: " + errorMessage);
        return errorMessage;
    }

    public TimedOutScreen waitForTimedOutScreen() {
        waitForTimeoutScreen();
        return this;
    }

    private WebElement waitForTimeoutScreen() {
        driver.waitTillElementIsVisible(BY_INTERNATIONAL_ROAMING_PLANS_HEADING_XPATH, 30);
        return driver.waitTillElementIsVisible(BY_SOMETHING_WENT_WRONG_MESSAGE_XPATH, 30);
    }
}
