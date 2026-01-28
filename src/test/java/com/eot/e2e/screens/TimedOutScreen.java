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
    protected static final By BY_BACK_BUTTON_XPATH = By.xpath("//div[@aria-label=\"button Back\"]");
    private static Driver driver;
    private static Visual visually;
    private static TestExecutionContext context;

    public static TimedOutScreen get() {
        driver = Drivers.getDriverForCurrentUser(Thread.currentThread().getId());
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

    public abstract String getSomethingWentWrongMessage();

    public abstract TimedOutScreen waitForTimedOutScreen();

    public JioHomeScreen clickOnBackButton() {
        LOGGER.info("Clicking on Back Button");
        driver.waitTillElementIsVisible(BY_BACK_BUTTON_XPATH).click();
        return JioHomeScreen.get();
    }
}
