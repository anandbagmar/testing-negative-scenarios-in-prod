package com.eot.e2e.steps;

import com.eot.e2e.businessLayer.JioBL;
import com.eot.e2e.businessLayer.RechargePlansBL;
import com.eot.e2e.entities.E2E_TEST_CONTEXT;
import com.znsio.teswiz.context.SessionContext;
import com.znsio.teswiz.context.TestExecutionContext;
import com.znsio.teswiz.runner.Drivers;
import com.znsio.teswiz.runner.Runner;
import io.cucumber.java.PendingException;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;

public class JioRechargeSteps {
    private static final Logger LOGGER = LogManager.getLogger(JioRechargeSteps.class.getName());
    private final TestExecutionContext context;

    public JioRechargeSteps() {
        context = SessionContext.getTestExecutionContext(Thread.currentThread().getId());
        LOGGER.info("context: " + context.getTestName());
    }

    @Given("I sign in as a registered {string}")
    public void iSignInAsARegistered(String userSuffix) {
        Map userDetails = Runner.getTestDataAsMap(userSuffix);
        LOGGER.info(System.out.printf(
                "iSignInAsARegistered - Persona:'%s', User details: '%s', Platform: '%s'",
                E2E_TEST_CONTEXT.I, userDetails, Runner.getPlatform()));
        Drivers.createDriverFor(E2E_TEST_CONTEXT.I, Runner.getPlatform(), context);
        context.addTestState(E2E_TEST_CONTEXT.I, String.valueOf(userDetails.get("username")));
        new JioBL(E2E_TEST_CONTEXT.I, Runner.getPlatform());
    }

    @Given("I have a prepaid number {string} with zero plans")
    public void iHaveAPrepaidNumberWithZeroPlans(String prepaidNumber) {
        String prepaidNumberToUse = Runner.getTestData(prepaidNumber);
        LOGGER.info(System.out.printf(
                "iHaveAPrepaidNumberWithZeroPlans - Persona:'%s', User details: '%s', Platform: '%s'",
                E2E_TEST_CONTEXT.I, prepaidNumberToUse, Runner.getPlatform()));
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
}
