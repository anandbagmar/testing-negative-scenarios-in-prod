package com.eot.e2e.screens.android;

import com.applitools.eyes.selenium.fluent.Target;
import com.eot.e2e.screens.JioHomeScreen;
import com.eot.e2e.screens.PrepaidPlansScreen;
import com.znsio.teswiz.context.TestExecutionContext;
import com.znsio.teswiz.runner.Driver;
import com.znsio.teswiz.runner.Runner;
import com.znsio.teswiz.runner.Visual;
import com.znsio.teswiz.tools.Wait;
import io.appium.java_client.AppiumBy;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import java.lang.reflect.Method;

public class JioHomeScreenAndroid extends JioHomeScreen {
    private final Driver driver;
    private final Visual visually;
    private final WebDriver innerDriver;
    private final TestExecutionContext context;
    private static final String SCREEN_NAME = JioHomeScreenAndroid.class.getSimpleName();
    private static final Logger LOGGER = LogManager.getLogger(SCREEN_NAME);
    private static final String NOT_YET_IMPLEMENTED = " not yet implemented";
    private static final By BY_IGNORE_CAROUSSEL_XPATH = By.xpath("//div[@class=\"slick-center-mode\"]");
    private static final By BY_RECHARGE_BUTTON_NATIVE_VIEW_ACCESSIBILITY_ID = AppiumBy.accessibilityId("home.btn.recharge");
    private static final By BY_INTERNATIONAL_ROAMING_PLANS_HEADING_XPATH = By.xpath("//button//div[text()=\"Gaming\"]");

    public JioHomeScreenAndroid(Driver driver, Visual visually) {
        this.driver = driver;
        this.visually = visually;
        this.innerDriver = this.driver.getInnerDriver();
        long threadId = Thread.currentThread().getId();
        context = Runner.getTestExecutionContext(threadId);
    }

    @Override
    public JioHomeScreen onLaunch() {
        LOGGER.info("Specmatic Android E2E Demo App");
        Wait.waitFor(3);
        visually.check(SCREEN_NAME, "Specmatic Android E2E Demo App", Target.window().fully().ignore(BY_IGNORE_CAROUSSEL_XPATH));
        switchToRechargePhoneNumber();
        return this;
    }

    private void switchToRechargePhoneNumber() {
        Wait.waitFor(2);
        visually.checkWindow(SCREEN_NAME, "Jio Home Page - Native View");
        driver.findElement(BY_RECHARGE_BUTTON_NATIVE_VIEW_ACCESSIBILITY_ID).click();
        Wait.waitFor(10);
        driver.setWebViewContext();
        Wait.waitFor(2);
        visually.checkWindow(SCREEN_NAME, "Jio Home Page");
    }

    @Override
    public PrepaidPlansScreen proceedToPlanSelection() {
        LOGGER.info("Proceeding to Plan Selection Page");
        clickOnProceedButton(BY_PROCEED_BUTTON_XPATH);
        driver.waitTillElementIsVisible(BY_INTERNATIONAL_ROAMING_PLANS_HEADING_XPATH);
        return PrepaidPlansScreen.get();
    }
}

