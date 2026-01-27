package com.eot.e2e.screens;

import com.eot.e2e.screens.android.PrepaidPlansScreenAndroid;
import com.eot.e2e.screens.web.PrepaidPlansScreenWeb;
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

public abstract class PrepaidPlansScreen {
    private static final String SCREEN_NAME = PrepaidPlansScreen.class.getSimpleName();
    private static final Logger LOGGER = LogManager.getLogger(SCREEN_NAME);
    private static final By BY_ERROR_MESSAGE_XPATH = By.xpath("//div[@data-testid=\"sadFaceError\"]");
    private static final By BY_NUMBER_OF_PLANS_FOUND_ID = By.id("planAcc0");
    private static Driver driver;
    private static Visual visually;
    private static TestExecutionContext context;

    public static PrepaidPlansScreen get() {
        driver = Drivers.getDriverForCurrentUser(Thread.currentThread().getId());
        Platform platform = Runner.fetchPlatform(Thread.currentThread().getId());
        LOGGER.info(SCREEN_NAME + ": Driver type: " + driver.getType() + ": Platform: " + platform);
        visually = Drivers.getVisualDriverForCurrentUser(Thread.currentThread().getId());
        long threadId = Thread.currentThread().getId();
        context = Runner.getTestExecutionContext(threadId);

        switch (platform) {
            case web:
                return new PrepaidPlansScreenWeb(driver, visually);
            case android:
                return new PrepaidPlansScreenAndroid(driver, visually);
        }
        throw new NotImplementedException(
                SCREEN_NAME + " is not implemented in " + Runner.getPlatform());
    }

    public String getErrorMessage() {
        LOGGER.info("Getting error message from Recharge Options Page");
        visually.checkWindow(SCREEN_NAME, "Recharge Options Page");
        String actualErrorMessage = driver.waitTillElementIsVisible(BY_ERROR_MESSAGE_XPATH).getText().replace("\n", "");
        LOGGER.info("Actual error message displayed: " + actualErrorMessage);
        return actualErrorMessage;
    }

    public String getNumberOfPlansAvailableMessage() {
        LOGGER.info("Getting number of plans seen from Recharge Options Page");
        visually.checkWindow(SCREEN_NAME, "Recharge Options Page");
        String actualNumberOfPlansAvailableMessage = driver.waitTillElementIsVisible(BY_NUMBER_OF_PLANS_FOUND_ID).getText().replace("\n", "");
        LOGGER.info("Actual number of plans available: " + actualNumberOfPlansAvailableMessage);
        return actualNumberOfPlansAvailableMessage;
    }
}
