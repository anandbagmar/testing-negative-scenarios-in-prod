package com.eot.e2e.screens;

import com.eot.e2e.screens.android.PaymentScreenAndroid;
import com.eot.e2e.screens.web.PaymentScreenWeb;
import com.znsio.teswiz.context.TestExecutionContext;
import com.znsio.teswiz.entities.Platform;
import com.znsio.teswiz.runner.Driver;
import com.znsio.teswiz.runner.Drivers;
import com.znsio.teswiz.runner.Runner;
import com.znsio.teswiz.runner.Visual;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class PaymentScreen {
    private static final String SCREEN_NAME = PaymentScreen.class.getSimpleName();
    private static final Logger LOGGER = LogManager.getLogger(SCREEN_NAME);
    private static Visual visually;
    private static Driver driver;
    private static TestExecutionContext context;

    public static PaymentScreen get() {
        driver = Drivers.getDriverForCurrentUser(Thread.currentThread().getId());
        Platform platform = Runner.fetchPlatform(Thread.currentThread().getId());
        LOGGER.info(SCREEN_NAME + ": Driver type: " + driver.getType() + ": Platform: " + platform);
        visually = Drivers.getVisualDriverForCurrentUser(Thread.currentThread().getId());
        long threadId = Thread.currentThread().getId();
        context = Runner.getTestExecutionContext(threadId);


        switch (platform) {
            case web:
                return new PaymentScreenWeb(driver, visually);
            case android:
                return new PaymentScreenAndroid(driver, visually);
        }
        throw new NotImplementedException(
                SCREEN_NAME + " is not implemented in " + Runner.getPlatform());
    }
}
