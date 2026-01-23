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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class JioPostpaidRechargeSteps {
    private static final Logger LOGGER = LogManager.getLogger(JioPostpaidRechargeSteps.class.getName());
    private final TestExecutionContext context;

    public JioPostpaidRechargeSteps() {
        context = SessionContext.getTestExecutionContext(Thread.currentThread().getId());
        LOGGER.info("context: " + context.getTestName());
    }

    @Given("I have a postpaid number")
    public void iHaveAPostpaidNumber() {
        LOGGER.info(System.out.printf(
                "iHaveAPostpaidNumber - Persona:'%s', Platform: '%s'",
                E2E_TEST_CONTEXT.I, Runner.getPlatform()));
        Drivers.createDriverFor(E2E_TEST_CONTEXT.I, Runner.getPlatform(), context);
        new JioBL(E2E_TEST_CONTEXT.I, Runner.getPlatform()).onLaunch();
    }

    @Given("I recharge with a valid recharge amount")
    public void iRechargeWithAValidRechargeAmount() {
        String postpaidNumberToUse = Runner.getTestData(E2E_TEST_CONTEXT.POSTPAID_VALID_RECHARGE_PHONE_NUMBER);
        String rechargeAmount = Runner.getTestData(E2E_TEST_CONTEXT.POSTPAID_VALID_RECHARGE_AMOUNT);
        context.addTestState(E2E_TEST_CONTEXT.RECHARGE_NUMBER, postpaidNumberToUse);
        context.addTestState(E2E_TEST_CONTEXT.RECHARGE_AMOUNT, rechargeAmount);
        LOGGER.info(System.out.printf(
                "iRechargeWithAValidRechargeAmount - Persona:'%s', Platform: '%s', Postpaid number: '%s', Recharge amount: '%s'",
                E2E_TEST_CONTEXT.I, Runner.getPlatform(), postpaidNumberToUse, rechargeAmount));
        new JioBL().enterPostpaidNumberAndValidRechargeAmount();
    }
    @Given("I recharge with an invalid recharge amount")
    public void iRechargeWithAnInvalidRechargeAmount() {
        String postpaidNumberToUse = Runner.getTestData(E2E_TEST_CONTEXT.POSTPAID_INVALID_RECHARGE_PHONE_NUMBER);
        String rechargeAmount = Runner.getTestData(E2E_TEST_CONTEXT.POSTPAID_INVALID_RECHARGE_AMOUNT);
        context.addTestState(E2E_TEST_CONTEXT.RECHARGE_NUMBER, postpaidNumberToUse);
        context.addTestState(E2E_TEST_CONTEXT.RECHARGE_AMOUNT, rechargeAmount);
        LOGGER.info(System.out.printf(
                "iRechargeWithAnInvalidRechargeAmount - Persona:'%s', Platform: '%s', Postpaid number: '%s', Recharge amount: '%s'",
                E2E_TEST_CONTEXT.I, Runner.getPlatform(), postpaidNumberToUse, rechargeAmount));
        new JioBL().enterPostpaidNumberAndInvalidRechargeAmount();
    }

    @Then("I should see {string} error message for invalid recharge amount")
    public void iShouldSeeErrorMessageForInvalidRechargeAmount(String expectedMessage) {
        String expectedInvalidRechargeErrorMessage = Runner.getTestData(E2E_TEST_CONTEXT.POSTPAID_MAXIMUM_AMOUNT_ERROR_MESSAGE);
        new JioBL().verifyInvalidRechargeAmountForPostpaidMessage(expectedInvalidRechargeErrorMessage);
    }
}
