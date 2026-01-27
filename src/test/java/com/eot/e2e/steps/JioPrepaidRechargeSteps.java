package com.eot.e2e.steps;

import com.eot.e2e.businessLayer.JioBL;
import com.eot.e2e.businessLayer.RechargePlansBL;
import com.eot.e2e.entities.E2E_TEST_CONTEXT;
import com.znsio.teswiz.context.SessionContext;
import com.znsio.teswiz.context.TestExecutionContext;
import com.znsio.teswiz.runner.Drivers;
import com.znsio.teswiz.runner.Runner;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class JioPrepaidRechargeSteps {
    private static final Logger LOGGER = LogManager.getLogger(JioPrepaidRechargeSteps.class.getName());
    private final TestExecutionContext context;

    public JioPrepaidRechargeSteps() {
        context = SessionContext.getTestExecutionContext(Thread.currentThread().getId());
    }

    @Given("I have a prepaid number {string} with {string} plans")
    public void iHaveAPrepaidNumberWithVaryingPlans(String prepaidNumber, String numberOfPlans) {
        LOGGER.info("Prepaid number to use: {}", prepaidNumber);
        String prepaidNumberToUse = Runner.getTestData(prepaidNumber);
        Drivers.createDriverFor(E2E_TEST_CONTEXT.I, Runner.getPlatform(), context);
        context.addTestState(E2E_TEST_CONTEXT.RECHARGE_NUMBER, prepaidNumberToUse);
        new JioBL(E2E_TEST_CONTEXT.I, Runner.getPlatform()).onLaunch();
    }

    @When("I recharge the prepaid number")
    public void iRechargeThePrepaidNumber() {
        new JioBL().enterPrepaidNumberAndRecharge();
    }

    @Then("I should see {string} error message")
    public void iShouldSeeErrorMessage(String errorMessage) {
        String expectedErrorMessage = Runner.getTestData(errorMessage);
        new RechargePlansBL().verifyErrorMessageIsDisplayed(expectedErrorMessage);
    }

    @Then("I should see {string} plans available message")
    public void iShouldSeePlansAvailableMessage(String numberOfPlans) {
        String expectedPlansAvailableMessage = Runner.getTestData(E2E_TEST_CONTEXT.PLANS_AVAILABLE_MESSAGE).replace(E2E_TEST_CONTEXT.NUMBER_OF_PLANS, numberOfPlans);
        new RechargePlansBL().verifyNumberOfPlansAvailable(expectedPlansAvailableMessage);
    }

    @Then("I should see {string} plan available message")
    public void iShouldSeePlanAvailableMessage(String numberOfPlans) {
        String expectedPlansAvailableMessage = Runner.getTestData(E2E_TEST_CONTEXT.PLANS_AVAILABLE_MESSAGE).replace(E2E_TEST_CONTEXT.NUMBER_OF_PLANS, numberOfPlans);
        new RechargePlansBL().verifyNumberOfPlansAvailable(expectedPlansAvailableMessage);
    }
}
