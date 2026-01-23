package com.eot.e2e.businessLayer;

import com.eot.e2e.entities.E2E_TEST_CONTEXT;
import com.eot.e2e.screens.PrepaidPlansScreen;
import com.znsio.teswiz.context.TestExecutionContext;
import com.znsio.teswiz.entities.Platform;
import com.znsio.teswiz.runner.Runner;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.assertj.core.api.SoftAssertions;

public class RechargePlansBL {
    private static final Logger LOGGER = LogManager.getLogger(RechargePlansBL.class.getName());
    private final TestExecutionContext context;
    private final SoftAssertions softly;
    private final String currentUserPersona;
    private final Platform currentPlatform;

    public RechargePlansBL(String userPersona, Platform forPlatform) {
        long threadId = Thread.currentThread().getId();
        this.context = Runner.getTestExecutionContext(threadId);
        softly = Runner.getSoftAssertion(threadId);
        this.currentUserPersona = userPersona;
        this.currentPlatform = forPlatform;
        Runner.setCurrentDriverForUser(userPersona, forPlatform, context);
    }

    public RechargePlansBL() {
        long threadId = Thread.currentThread().getId();
        this.context = Runner.getTestExecutionContext(threadId);
        softly = Runner.getSoftAssertion(threadId);
        this.currentUserPersona = E2E_TEST_CONTEXT.I;
        this.currentPlatform = Runner.getPlatform();
    }

    public RechargePlansBL verifyErrorMessageIsDisplayed(String expectedErrorMessage) {
        LOGGER.info("Verifying error message is displayed: " + expectedErrorMessage);
        String actualErrorMessage = PrepaidPlansScreen.get().getErrorMessage();
        softly.assertThat(actualErrorMessage)
                .as("Expected error message to be: " + expectedErrorMessage + " but was: " + actualErrorMessage)
                .isEqualTo(expectedErrorMessage);
        return this;
    }

    public RechargePlansBL verifyNumberOfPlansAvailable(String expectedPlansAvailableMessage) {
        LOGGER.info("Verify number of plans available: " + expectedPlansAvailableMessage);
        String actualNumberOfPlansAvailableMessage = PrepaidPlansScreen.get().getNumberOfPlansAvailableMessage();
        softly.assertThat(actualNumberOfPlansAvailableMessage)
                .as("Expected: " + expectedPlansAvailableMessage + " but was: " + actualNumberOfPlansAvailableMessage)
                .isEqualTo(expectedPlansAvailableMessage);
        return this;
    }
}
