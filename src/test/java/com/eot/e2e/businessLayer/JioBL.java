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
        JioHomeScreen.get().enterPrepaidNumber().proceedToPlanSelection();
        return new RechargePlansBL(this.currentUserPersona, this.currentPlatform);
    }

    public JioBL onLaunch() {
        JioHomeScreen.get().onLaunch();
        return this;
    }
}
