package com.eot.e2e.screens.web;

import com.eot.e2e.screens.PaymentScreen;
import com.znsio.teswiz.context.TestExecutionContext;
import com.znsio.teswiz.runner.Driver;
import com.znsio.teswiz.runner.Runner;
import com.znsio.teswiz.runner.Visual;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;

public class PaymentScreenWeb extends PaymentScreen {
    private final Driver driver;
    private final Visual visually;
    private final WebDriver innerDriver;
    private final TestExecutionContext context;
    private static final String SCREEN_NAME = JioHomeScreenWeb.class.getSimpleName();
    private static final Logger LOGGER = LogManager.getLogger(SCREEN_NAME);
    private static final String NOT_YET_IMPLEMENTED = " not yet implemented";

    public PaymentScreenWeb(Driver driver, Visual visually) {
        this.driver = driver;
        this.visually = visually;
        this.innerDriver = this.driver.getInnerDriver();
        long threadId = Thread.currentThread().getId();
        context = Runner.getTestExecutionContext(threadId);
    }

}
