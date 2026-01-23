package com.eot.e2e.businessLayer;

import com.eot.e2e.entities.E2E_TEST_CONTEXT;
import com.eot.e2e.screens.JioHomeScreen;
import com.znsio.teswiz.context.TestExecutionContext;
import com.znsio.teswiz.entities.Platform;
import com.znsio.teswiz.runner.Runner;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.assertj.core.api.SoftAssertions;

public class JioBL {
    private static final Logger LOGGER = LogManager.getLogger(JioBL.class.getName());
    private final TestExecutionContext context;
    private final SoftAssertions softly;
    private final String currentUserPersona;
    private final Platform currentPlatform;

    public JioBL(String userPersona, Platform forPlatform) {
        long threadId = Thread.currentThread().getId();
        this.context = Runner.getTestExecutionContext(threadId);
        softly = Runner.getSoftAssertion(threadId);
        this.currentUserPersona = userPersona;
        this.currentPlatform = forPlatform;
        Runner.setCurrentDriverForUser(userPersona, forPlatform, context);
    }

    public JioBL() {
        long threadId = Thread.currentThread().getId();
        this.context = Runner.getTestExecutionContext(threadId);
        softly = Runner.getSoftAssertion(threadId);
        this.currentUserPersona = E2E_TEST_CONTEXT.I;
        this.currentPlatform = Runner.getPlatform();
    }

    public RechargePlansBL enterPrepaidNumberAndRecharge() {
        JioHomeScreen.get()
                .enterPrepaidNumber()
                .proceedToPlanSelection();
        return new RechargePlansBL(this.currentUserPersona, this.currentPlatform);
    }

    public JioBL onLaunch() {
        JioHomeScreen.get().onLaunch();
        return this;
    }

    public PaymentsBL enterPostpaidNumberAndValidRechargeAmount() {
        JioHomeScreen.get()
                .enterPostPaidNumber()
                .proceedToEnterRechargeAmount()
                .enterValidRechargeAmount()
                .proceedToPaymentForValidRechargeAmount();
        return new PaymentsBL(this.currentUserPersona, this.currentPlatform);
    }

    public JioBL enterPostpaidNumberAndInvalidRechargeAmount() {
        String actualInvalidRechargeAmountErrorMessage = JioHomeScreen.get()
                .enterPostPaidNumber()
                .proceedToEnterRechargeAmount()
                .enterInvalidRechargeAmount()
                .proceedToPaymentForInvalidRechargeAmount()
                .getInvalidRechargeAmountErrorMessage();
        context.addTestState(E2E_TEST_CONTEXT.POSTPAID_MAXIMUM_AMOUNT_ERROR_MESSAGE, actualInvalidRechargeAmountErrorMessage);
        return this;
    }

    public JioBL verifyInvalidRechargeAmountForPostpaidMessage(String expectedInvalidRechargeErrorMessage) {
        String actualInvalidRechargeErrorMessage = context.getTestStateAsString(E2E_TEST_CONTEXT.POSTPAID_MAXIMUM_AMOUNT_ERROR_MESSAGE);
        softly.assertThat(actualInvalidRechargeErrorMessage)
                .as("Verify invalid recharge amount error message")
                .isEqualTo(expectedInvalidRechargeErrorMessage);
        return this;
    }

    public JioBL enterNonJioNumberAndRecharge() {
        String actualInvalidJioNumberErrorMessage = JioHomeScreen.get()
                .enterNonJioNumber()
                .proceedToRechargeNonJioNumber()
                .getInvalidJioNumberErrorMessage();
        context.addTestState(E2E_TEST_CONTEXT.ACTUAL_INVALID_JIO_NUMBER_ERROR_MESSAGE, actualInvalidJioNumberErrorMessage);
        return this;
    }

    public JioBL verifyInvalidJioNumberMessage(String expectedErrorMessage) {
        String actualInvalidRechargeErrorMessage = context.getTestStateAsString(E2E_TEST_CONTEXT.ACTUAL_INVALID_JIO_NUMBER_ERROR_MESSAGE);
        softly.assertThat(actualInvalidRechargeErrorMessage)
                .as("Verify invalid Jio number error message")
                .isEqualTo(expectedErrorMessage);
        return this;
    }
}
