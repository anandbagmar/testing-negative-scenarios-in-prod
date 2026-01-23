package com.eot.e2e.screens.web;

import com.eot.e2e.screens.TimedOutScreen;
import com.znsio.teswiz.context.TestExecutionContext;
import com.znsio.teswiz.runner.Driver;
import com.znsio.teswiz.runner.Runner;
import com.znsio.teswiz.runner.Visual;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class TimedOutScreenWeb extends TimedOutScreen {
    private final Driver driver;
    private final Visual visually;
    private final WebDriver innerDriver;
    private final TestExecutionContext context;
    private static final String SCREEN_NAME = TimedOutScreenWeb.class.getSimpleName();
    private static final Logger LOGGER = LogManager.getLogger(SCREEN_NAME);
    private static final String NOT_YET_IMPLEMENTED = " not yet implemented";
    private static final By BY_SOMETHING_WENT_WRONG_MESSAGE_XPATH = By.xpath("//div[contains(@class,\"jdsErrorScreen_containerLayout_\")]");
    private static final By BY_INTERNATIONAL_ROAMING_PLANS_HEADING_XPATH = By.xpath("//a[@aria-label=\"International Services\"]");

    public TimedOutScreenWeb(Driver driver, Visual visually) {
        this.driver = driver;
        this.visually = visually;
        this.innerDriver = this.driver.getInnerDriver();
        long threadId = Thread.currentThread().getId();
        context = Runner.getTestExecutionContext(threadId);
    }

    @Override
    public String getSomethingWentWrongMessage() {
        LOGGER.info("Getting 'Something went wrong' message from Timed Out Screen");
        visually.checkWindow(SCREEN_NAME, "Timed Out Screen");
        String errorMessage = waitForTimeoutScreen().getText().replace("\n", " ");
        LOGGER.info("'Something went wrong' message: " + errorMessage);
        return errorMessage;
    }

    @Override
    public TimedOutScreen waitForTimedOutScreen() {
        waitForTimeoutScreen();
        return this;
    }

    private WebElement waitForTimeoutScreen() {
        driver.waitTillElementIsVisible(BY_INTERNATIONAL_ROAMING_PLANS_HEADING_XPATH, 30);
        return driver.waitTillElementIsVisible(BY_SOMETHING_WENT_WRONG_MESSAGE_XPATH, 30);
    }
}
