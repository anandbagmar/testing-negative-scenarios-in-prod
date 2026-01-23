package com.eot.e2e.steps;

import com.eot.e2e.businessLayer.JioBL;
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

public class InvalidJioNumberSteps {
    private static final Logger LOGGER = LogManager.getLogger(InvalidJioNumberSteps.class.getName());
    private final TestExecutionContext context;

    public InvalidJioNumberSteps() {
        context = SessionContext.getTestExecutionContext(Thread.currentThread().getId());
    }

    @Given("I have a non-Jio number")
    public void iHaveANonJioNumber() {
        String nonJioNumberToUse = Runner.getTestData(E2E_TEST_CONTEXT.INVALID_PHONE_NUMBER);
        String expectedErrorMessage = Runner.getTestData(E2E_TEST_CONTEXT.EXPECTED_INVALID_PHONE_NUMBER_ERROR_MESSAGE);
        context.addTestState(E2E_TEST_CONTEXT.RECHARGE_NUMBER, nonJioNumberToUse);
        context.addTestState(E2E_TEST_CONTEXT.EXPECTED_INVALID_PHONE_NUMBER_ERROR_MESSAGE, expectedErrorMessage);
        Drivers.createDriverFor(E2E_TEST_CONTEXT.I, Runner.getPlatform(), context);
        new JioBL(E2E_TEST_CONTEXT.I, Runner.getPlatform()).onLaunch();
    }

    @When("I recharge the non-Jio number")
    public void iRechargeTheNonJioNumber() {
        new JioBL().enterNonJioNumberAndRecharge();
    }

    @Then("I should see an invalid Jio number error message")
    public void iShouldSeeAnInvalidJioNumberErrorMessage() {
        String expectedErrorMessage = context.getTestStateAsString(E2E_TEST_CONTEXT.EXPECTED_INVALID_PHONE_NUMBER_ERROR_MESSAGE);
        new JioBL().verifyInvalidJioNumberMessage(expectedErrorMessage);
    }
}
