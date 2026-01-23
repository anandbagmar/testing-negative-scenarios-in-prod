package com.eot.e2e.screens.web;

import com.eot.e2e.screens.PrepaidPlansScreen;
import com.znsio.teswiz.context.TestExecutionContext;
import com.znsio.teswiz.runner.Driver;
import com.znsio.teswiz.runner.Runner;
import com.znsio.teswiz.runner.Visual;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class PrepaidPlansScreenWeb extends PrepaidPlansScreen {
    private final Driver driver;
    private final Visual visually;
    private final WebDriver innerDriver;
    private final TestExecutionContext context;
    private static final String SCREEN_NAME = JioHomeScreenWeb.class.getSimpleName();
    private static final Logger LOGGER = LogManager.getLogger(SCREEN_NAME);
    private static final String NOT_YET_IMPLEMENTED = " not yet implemented";
    private static final By BY_ERROR_MESSAGE_XPATH = By.xpath("//div[@data-testid=\"sadFaceError\"]");
    private static final By BY_NUMBER_OF_PLANS_FOUND_ID = By.id("planAcc0");

    public PrepaidPlansScreenWeb(Driver driver, Visual visually) {
        this.driver = driver;
        this.visually = visually;
        this.innerDriver = this.driver.getInnerDriver();
        long threadId = Thread.currentThread().getId();
        context = Runner.getTestExecutionContext(threadId);
    }

    @Override
    public String getErrorMessage() {
        LOGGER.info("Getting error message from Recharge Options Page");
        visually.checkWindow(SCREEN_NAME, "Recharge Options Page");
        String actualErrorMessage = driver.waitTillElementIsVisible(BY_ERROR_MESSAGE_XPATH).getText().replace("\n", "");
        LOGGER.info("Actual error message displayed: " + actualErrorMessage);
        return actualErrorMessage;
    }

    @Override
    public String getNumberOfPlansAvailableMessage() {
        LOGGER.info("Getting number of plans seen from Recharge Options Page");
        visually.checkWindow(SCREEN_NAME, "Recharge Options Page");
        String actualNumberOfPlansAvailableMessage = driver.waitTillElementIsVisible(BY_NUMBER_OF_PLANS_FOUND_ID).getText().replace("\n", "");
        LOGGER.info("Actual number of plans available: " + actualNumberOfPlansAvailableMessage);
        return actualNumberOfPlansAvailableMessage;
    }
}
