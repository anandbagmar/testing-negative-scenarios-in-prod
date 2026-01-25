package com.eot.e2e.screens.android;

import com.eot.e2e.screens.JioHomeScreen;
import com.eot.e2e.screens.PaymentScreen;
import com.eot.e2e.screens.PrepaidPlansScreen;
import com.eot.e2e.screens.TimedOutScreen;
import com.eot.e2e.screens.web.JioHomeScreenWeb;
import com.znsio.teswiz.context.TestExecutionContext;
import com.znsio.teswiz.runner.Driver;
import com.znsio.teswiz.runner.Runner;
import com.znsio.teswiz.runner.Visual;
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
    private static final String SCREEN_NAME = JioHomeScreenWeb.class.getSimpleName();
    private static final Logger LOGGER = LogManager.getLogger(SCREEN_NAME);
    private static final String NOT_YET_IMPLEMENTED = " not yet implemented";
    private static final By BY_IGNORE_CAROUSSEL_XPATH = By.xpath("//div[@class=\"slick-center-mode\"]");
    private static final By BY_JIONUMBER_TEXT_BOX_XPATH = By.xpath("//input[@data-testid='JDSInput-input']");
    private static final By BY_RECHARGE_OR_PAY_BILLS_HEADING_XPATH = By.xpath("//div[text()='Recharge or pay bills']");
    private static final By BY_RECHARGE_NUMBER_SECTION_XPATH = By.xpath("//div[@class=\"recharge-paybill-withleads\"]");
    private static final By BY_PROCEED_BUTTON_XPATH = By.xpath("//div[text()='Proceed']");
    private static final By BY_INTERNATIONAL_ROAMING_PLANS_HEADING_XPATH = By.xpath("//button[text()=\"International Roaming\"]");
    private static final By BY_PAY_BUTTON_XPATH = By.xpath("//div[text()=\"Pay\"]");
    private static final By BY_RECHARGE_AMOUNT_TEXT_BOX_XPATH = By.xpath("//input[@placeholder=\"Amount\"]");
    private static final By BY_INVALID_RECHARGE_AMOUNT_ERROR_MESSAGE_CLASSNAME = By.className("j-error-message");

    public JioHomeScreenAndroid(Driver driver, Visual visually) {
        this.driver = driver;
        this.visually = visually;
        this.innerDriver = this.driver.getInnerDriver();
        long threadId = Thread.currentThread().getId();
        context = Runner.getTestExecutionContext(threadId);
    }

    @Override
    public JioHomeScreen enterPrepaidNumber() {
        throw new NotImplementedException(Method.class.getSimpleName() + " not yet implemented");
    }

    @Override
    public JioHomeScreen onLaunch() {
        throw new NotImplementedException(Method.class.getSimpleName() + " not yet implemented");
    }

    @Override
    public PrepaidPlansScreen proceedToPlanSelection() {
        throw new NotImplementedException(Method.class.getSimpleName() + " not yet implemented");
    }

    @Override
    public JioHomeScreen enterPostPaidNumber() {
        throw new NotImplementedException(Method.class.getSimpleName() + " not yet implemented");
    }

    @Override
    public JioHomeScreen enterValidRechargeAmount() {
        throw new NotImplementedException(Method.class.getSimpleName() + " not yet implemented");
    }

    @Override
    public JioHomeScreen proceedToEnterRechargeAmount() {
        throw new NotImplementedException(Method.class.getSimpleName() + " not yet implemented");
    }

    @Override
    public PaymentScreen proceedToPaymentForValidRechargeAmount() {
        throw new NotImplementedException(Method.class.getSimpleName() + " not yet implemented");
    }

    @Override
    public JioHomeScreen enterInvalidRechargeAmount() {
        throw new NotImplementedException(Method.class.getSimpleName() + " not yet implemented");
    }

    @Override
    public String getInvalidRechargeAmountErrorMessage() {
        return "";
    }

    @Override
    public JioHomeScreen proceedToPaymentForInvalidRechargeAmount() {
        throw new NotImplementedException(Method.class.getSimpleName() + " not yet implemented");
    }

    @Override
    public JioHomeScreen enterNonJioNumber() {
        throw new NotImplementedException(Method.class.getSimpleName() + " not yet implemented");
    }

    @Override
    public JioHomeScreen proceedToRechargeNonJioNumber() {
        throw new NotImplementedException(Method.class.getSimpleName() + " not yet implemented");
    }

    @Override
    public String getInvalidJioNumberErrorMessage() {
        return "";
    }

    @Override
    public TimedOutScreen proceedToPlanSelectionWithoutWaiting() {
        throw new NotImplementedException(Method.class.getSimpleName() + " not yet implemented");
    }
}

