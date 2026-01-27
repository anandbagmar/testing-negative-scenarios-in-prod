package com.eot.e2e.screens;

import com.applitools.eyes.selenium.fluent.Target;
import com.eot.e2e.entities.E2E_TEST_CONTEXT;
import com.eot.e2e.screens.android.JioHomeScreenAndroid;
import com.eot.e2e.screens.web.JioHomeScreenWeb;
import com.znsio.teswiz.context.TestExecutionContext;
import com.znsio.teswiz.entities.Platform;
import com.znsio.teswiz.runner.Driver;
import com.znsio.teswiz.runner.Drivers;
import com.znsio.teswiz.runner.Runner;
import com.znsio.teswiz.runner.Visual;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import static com.eot.utilities.Wait.waitFor;

public abstract class JioHomeScreen {
    private static final String SCREEN_NAME = JioHomeScreen.class.getSimpleName();
    private static final Logger LOGGER = LogManager.getLogger(SCREEN_NAME);
    private static TestExecutionContext context;
    private static Driver driver;
    private static Visual visually;

    private static final By BY_IGNORE_CAROUSSEL_XPATH = By.xpath("//div[@class=\"slick-center-mode\"]");
    private static final By BY_JIONUMBER_TEXT_BOX_XPATH = By.xpath("//input[@data-testid='JDSInput-input']");
    private static final By BY_RECHARGE_OR_PAY_BILLS_HEADING_XPATH = By.xpath("//div[text()='Recharge or pay bills']");
    private static final By BY_RECHARGE_NUMBER_SECTION_XPATH = By.xpath("//div[@class=\"recharge-paybill-withleads\"]");
    private static final By BY_PROCEED_BUTTON_XPATH = By.xpath("//div[text()='Proceed']");
    private static final By BY_INTERNATIONAL_ROAMING_PLANS_HEADING_XPATH = By.xpath("//button[text()=\"International Roaming\"]");
    private static final By BY_PAY_BUTTON_XPATH = By.xpath("//div[text()=\"Pay\"]");
    private static final By BY_RECHARGE_AMOUNT_TEXT_BOX_XPATH = By.xpath("//input[@placeholder=\"Amount\"]");
    private static final By BY_INVALID_RECHARGE_AMOUNT_ERROR_MESSAGE_CLASSNAME = By.className("j-error-message");


    public static JioHomeScreen get() {
        driver = Drivers.getDriverForCurrentUser(Thread.currentThread().getId());
        Platform platform = Runner.fetchPlatform(Thread.currentThread().getId());
        LOGGER.info(SCREEN_NAME + ": Driver type: " + driver.getType() + ": Platform: " + platform);
        visually = Drivers.getVisualDriverForCurrentUser(Thread.currentThread().getId());
        long threadId = Thread.currentThread().getId();
        context = Runner.getTestExecutionContext(threadId);

        switch (platform) {
            case web:
                return new JioHomeScreenWeb(driver, visually);
            case android:
                return new JioHomeScreenAndroid(driver, visually);
        }
        throw new NotImplementedException(
                SCREEN_NAME + " is not implemented in " + Runner.getPlatform());
    }

    public abstract JioHomeScreen onLaunch();

    public JioHomeScreen enterPrepaidNumber() {
        String prepaidNumber = context.getTestStateAsString(E2E_TEST_CONTEXT.RECHARGE_NUMBER);
        LOGGER.info("Entering Prepaid Number: " + prepaidNumber);
        return enterNumber(prepaidNumber);
    }

    public JioHomeScreen enterPostPaidNumber() {
        String postpaidNumber = context.getTestStateAsString(E2E_TEST_CONTEXT.RECHARGE_NUMBER);
        LOGGER.info("Entering Postpaid Number: " + postpaidNumber);
        return enterNumber(postpaidNumber);
    }

    public JioHomeScreen enterValidRechargeAmount() {
        return enterRechargeAmount();
    }

    public JioHomeScreen proceedToEnterRechargeAmount() {
        clickOnProceedButton(BY_PROCEED_BUTTON_XPATH);
        driver.waitTillElementIsVisible(BY_PAY_BUTTON_XPATH);
        return this;
    }

    public PaymentScreen proceedToPaymentForValidRechargeAmount() {
        LOGGER.info("Proceeding to Payment Page");
        driver.waitForClickabilityOf(BY_PAY_BUTTON_XPATH).click();
        waitFor(5);
        visually.checkWindow(SCREEN_NAME, "Payment Page");
        return PaymentScreen.get();
    }

    public JioHomeScreen enterInvalidRechargeAmount() {
        return enterRechargeAmount();
    }

    public String getInvalidRechargeAmountErrorMessage() {
        String actualInvalidRechargeAmountErrorMessage = driver.waitTillElementIsVisible(BY_INVALID_RECHARGE_AMOUNT_ERROR_MESSAGE_CLASSNAME).getText();
        LOGGER.info("Actual Invalid Recharge Amount Error Message: " + actualInvalidRechargeAmountErrorMessage);
        visually.check(SCREEN_NAME, "Invalid Recharge Amount Error Message", Target.region(BY_RECHARGE_NUMBER_SECTION_XPATH));
        return actualInvalidRechargeAmountErrorMessage;
    }

    public JioHomeScreen proceedToPaymentForInvalidRechargeAmount() {
        LOGGER.info("Click on Pay Button to proceed");
        driver.waitForClickabilityOf(BY_PAY_BUTTON_XPATH).click();
        driver.waitTillElementIsVisible(BY_INVALID_RECHARGE_AMOUNT_ERROR_MESSAGE_CLASSNAME);
        visually.checkWindow(SCREEN_NAME, "Payment Page");
        return this;
    }

    public JioHomeScreen enterNonJioNumber() {
        String nonJioNumber = context.getTestStateAsString(E2E_TEST_CONTEXT.RECHARGE_NUMBER);
        LOGGER.info("Entering Non Jio Number: " + nonJioNumber);
        return enterNumber(nonJioNumber);
    }

    public JioHomeScreen proceedToRechargeNonJioNumber() {
        LOGGER.info("Proceeding to try and recharge Non Jio Number");
        clickOnProceedButton(BY_PROCEED_BUTTON_XPATH);
        driver.waitTillElementIsVisible(BY_INVALID_RECHARGE_AMOUNT_ERROR_MESSAGE_CLASSNAME);
        return this;
    }

    public String getInvalidJioNumberErrorMessage() {
        String actualErrorMessage = driver.waitTillElementIsVisible(BY_INVALID_RECHARGE_AMOUNT_ERROR_MESSAGE_CLASSNAME).getText();
        LOGGER.info("Actual Invalid Jio Number Error Message: " + actualErrorMessage);
        visually.check(SCREEN_NAME, "Invalid Jio Number Error Message", Target.region(BY_RECHARGE_NUMBER_SECTION_XPATH));
        return actualErrorMessage;
    }

    public TimedOutScreen proceedToPlanSelectionWithoutWaiting() {
        LOGGER.info("Proceeding to Plan Selection Page");
        clickOnProceedButton(BY_PROCEED_BUTTON_XPATH);
        return TimedOutScreen.get();
    }

    private void clickOnProceedButton(By byProceedButtonXpath) {
        driver.waitForClickabilityOf(byProceedButtonXpath).click();
    }

    private @NotNull JioHomeScreen enterRechargeAmount() {
        LOGGER.info("Proceeding to enter Recharge Amount");
        String rechargeAmount = context.getTestStateAsString(E2E_TEST_CONTEXT.RECHARGE_AMOUNT);
        typeInTextBox(BY_RECHARGE_AMOUNT_TEXT_BOX_XPATH, rechargeAmount);
        visually.check(SCREEN_NAME, "Entered Recharge Amount", Target.region(BY_RECHARGE_NUMBER_SECTION_XPATH));
        return this;
    }

    private @NotNull JioHomeScreen enterNumber(String postpaidNumber) {
        driver.scrollTillElementIntoView(BY_RECHARGE_OR_PAY_BILLS_HEADING_XPATH);
        typeInTextBox(BY_JIONUMBER_TEXT_BOX_XPATH, postpaidNumber);
        visually.check(SCREEN_NAME, "Entered Mobile Number", Target.region(BY_RECHARGE_NUMBER_SECTION_XPATH));
        return this;
    }

    private void typeInTextBox(By locator, String inputText) {
        LOGGER.info("Typing text: " + inputText + " into element: " + locator.toString());
        WebElement mobileNumberInput = driver.waitTillElementIsVisible(locator);

        for (char digit : inputText.toCharArray()) {
            mobileNumberInput.sendKeys(Character.toString(digit));
        }
        waitFor(1);
        LOGGER.info("Typed text: " + inputText + " into element: " + locator.toString());
    }

    public PrepaidPlansScreen proceedToPlanSelection() {
        LOGGER.info("Proceeding to Plan Selection Page");
        clickOnProceedButton(BY_PROCEED_BUTTON_XPATH);
        driver.waitTillElementIsVisible(BY_INTERNATIONAL_ROAMING_PLANS_HEADING_XPATH);
        return PrepaidPlansScreen.get();
    }

}
