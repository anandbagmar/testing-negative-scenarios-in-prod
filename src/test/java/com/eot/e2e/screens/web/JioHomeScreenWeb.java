package com.eot.e2e.screens.web;

import com.applitools.eyes.selenium.fluent.Target;
import com.eot.e2e.entities.E2E_TEST_CONTEXT;
import com.eot.e2e.screens.JioHomeScreen;
import com.eot.e2e.screens.PrepaidPlansScreen;
import com.znsio.teswiz.context.TestExecutionContext;
import com.znsio.teswiz.runner.Driver;
import com.znsio.teswiz.runner.Runner;
import com.znsio.teswiz.runner.Visual;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import static com.eot.utilities.Wait.waitFor;

public class JioHomeScreenWeb extends JioHomeScreen {
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

    public JioHomeScreenWeb(Driver driver, Visual visually) {
        this.driver = driver;
        this.visually = visually;
        this.innerDriver = this.driver.getInnerDriver();
        long threadId = Thread.currentThread().getId();
        context = Runner.getTestExecutionContext(threadId);
    }

    @Override
    public JioHomeScreen enterPrepaidNumber() {
        String prepaidNumber = context.getTestStateAsString(E2E_TEST_CONTEXT.RECHARGE_NUMBER);
        LOGGER.info("Entering Prepaid Number: " + prepaidNumber);
        driver.scrollTillElementIntoView(BY_RECHARGE_OR_PAY_BILLS_HEADING_XPATH);
        typeInTextBox(BY_JIONUMBER_TEXT_BOX_XPATH, prepaidNumber);
        waitFor(2);
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

    @Override
    public JioHomeScreen onLaunch() {
        LOGGER.info("On Jio Home Page");
        waitFor(3);
        visually.check(SCREEN_NAME, "Jio Home Page", Target.window().fully().ignore(BY_IGNORE_CAROUSSEL_XPATH));
        return this;
    }

    @Override
    public PrepaidPlansScreen proceedToPlanSelection() {
        LOGGER.info("Proceeding to Plan Selection Page");
        driver.waitForClickabilityOf(BY_PROCEED_BUTTON_XPATH).click();
        driver.waitTillElementIsVisible(BY_INTERNATIONAL_ROAMING_PLANS_HEADING_XPATH);
        return PrepaidPlansScreen.get();
    }
}
