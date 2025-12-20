package com.eot.e2e.negative;

import com.applitools.eyes.*;
import com.applitools.eyes.selenium.BrowserType;
import com.applitools.eyes.selenium.Configuration;
import com.applitools.eyes.selenium.Eyes;
import com.applitools.eyes.selenium.fluent.Target;
import com.applitools.eyes.visualgrid.model.DeviceName;
import com.applitools.eyes.visualgrid.services.RunnerOptions;
import com.applitools.eyes.visualgrid.services.VisualGridRunner;
import com.eot.utilities.Browser;
import com.eot.utilities.Driver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.*;

import java.io.File;
import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.eot.utilities.EyesResults.displayVisualValidationResults;
import static com.eot.utilities.Wait.*;

public class JioRecharge_UFG_Test {

    private static final String appName = JioRecharge_UFG_Test.class.getSimpleName();
    private static final String userName = System.getProperty("user.name");
    private static final String APPLITOOLS_API_KEY = System.getenv("APPLITOOLS_API_KEY");
    private static VisualGridRunner visualGridRunner;
    private static BatchInfo batch;
    private Eyes eyes;
    private WebDriver driver;
    private static final boolean USE_UFG = true;
    private static final boolean DISABLE_EYES = false;
    private static final String POSTPAID_PHONE_NUMBER = "2222222222";
//    private static final String POSTPAID_PHONE_NUMBER = "8766623339";
    private static final String POSTPAID_VALID_RECHARGE_AMOUNT = "50";
    private static final String POSTPAID_INVALID_RECHARGE_AMOUNT = "5000";
    private static final String PREPAID_PHONE_NUMBER = "1111111111";
//    private static final String PREPAID_PHONE_NUMBER = "9326223699";

    @BeforeSuite
    public static void beforeSuite() {
        System.out.println("BeforeSuite");
        visualGridRunner = new VisualGridRunner(new RunnerOptions().testConcurrency(10));
        visualGridRunner.setDontCloseBatches(true);
        batch = new BatchInfo(appName);
        batch.setNotifyOnCompletion(false);
        batch.setSequenceName(JioRecharge_UFG_Test.class.getSimpleName());
        batch.addProperty("REPOSITORY_NAME", new File(System.getProperty("user.dir")).getName());
        batch.addProperty("APP_NAME", appName);
        batch.addProperty("username", userName);
    }

    @AfterSuite
    public static void afterSuite() {
        System.out.println("AfterSuite");
        if (null != visualGridRunner) {
            System.out.println("Closing VisualGridRunner");
            visualGridRunner.close();
        }
        if (null != batch) {
            System.out.println("Mark batch completed");
            batch.setCompleted(true);
        }
    }

    @BeforeMethod
    public void beforeMethod(Method testInfo) {
        System.out.println("BeforeMethod: Test: " + testInfo.getName());
        driver = Driver.createDriverFor(Browser.CHROME);

        eyes = new Eyes(visualGridRunner);
        Configuration config = new Configuration();

        config.setApiKey(APPLITOOLS_API_KEY);
        config.setBatch(batch);
        config.setIsDisabled(DISABLE_EYES);
        config.setSaveNewTests(true);
        config.setMatchLevel(MatchLevel.STRICT);
        config.setIgnoreDisplacements(true);
        config.addProperty("username", userName);

        if (USE_UFG) {
            configureBrowersForUFG(config);
        }

        eyes.setConfiguration(config);
        eyes.setLogHandler(new StdoutLogHandler(true));
        eyes.setWaitBeforeScreenshots(1500);

        eyes.open(driver, appName, testInfo.getName(), new RectangleSize(1600, 1200));
    }

    private static void configureBrowersForUFG(Configuration config) {
        // Add browsers with different viewports
        config.addBrowser(1600, 1200, BrowserType.CHROME);
//        config.addBrowser(1920, 1600, BrowserType.FIREFOX);
//        config.addBrowser(1600, 1200, BrowserType.EDGE_CHROMIUM);
//        config.addBrowser(1920, 1600, BrowserType.SAFARI);
//        config.addBrowser(800, 600, BrowserType.CHROME);
//        config.addBrowser(800, 600, BrowserType.CHROME_ONE_VERSION_BACK);
//        config.addBrowser(800, 600, BrowserType.CHROME_TWO_VERSIONS_BACK);
//        config.addBrowser(700, 500, BrowserType.FIREFOX);
//        config.addBrowser(700, 500, BrowserType.SAFARI);
//        config.addBrowser(700, 500, BrowserType.EDGE_CHROMIUM);
//
//        // Add mobile emulation devices in Portrait/Landscape mode
//        config.addDeviceEmulation(DeviceName.iPhone_15_Pro);
//        config.addDeviceEmulation(DeviceName.Pixel_5);
    }

    @AfterMethod
    void afterMethod(Method testInfo) {
        System.out.println("AfterMethod: Test: " + testInfo.getName());
        AtomicBoolean isPass = new AtomicBoolean(true);
        if (null != eyes) {
            eyes.closeAsync();
            TestResultsSummary allTestResults = visualGridRunner.getAllTestResults(false);
            allTestResults.forEach(testResultContainer -> {
                System.out.printf("Test: %s\n%s%n", testResultContainer.getTestResults().getName(), testResultContainer);
                displayVisualValidationResults(testResultContainer.getTestResults());
                TestResultsStatus testResultsStatus = testResultContainer.getTestResults().getStatus();
                if (testResultsStatus.equals(TestResultsStatus.Failed) || testResultsStatus.equals(TestResultsStatus.Unresolved)) {
                    isPass.set(false);
                }
            });
        }
        if (null != driver) {
            driver.quit();
        }
//        Assert.assertTrue(isPass.get(), "Visual differences found.");
    }

    @Test (alwaysRun = true)
    void postPaidValidRechargeTest() {
        driver.get("https://www.jio.com/");
        eyes.checkWindow("Jio Home Page");

        waitFor(3);
        By jionumberTextBox = By.xpath("//input[@data-testid='JDSInput-input']");
        scrollTillElementIntoView(driver, jionumberTextBox);

        typeInTextBox(driver, jionumberTextBox, POSTPAID_PHONE_NUMBER);
        eyes.checkWindow("Entered Mobile Number");

        waitTillElementIsClickable(driver, By.xpath("//div[text()='Proceed']")).click();
        waitFor(3);

        By amountTextBox = By.xpath("//input[@placeholder='Amount']");
        waitTillElementIsPresent(driver, amountTextBox);
        typeInTextBox(driver, amountTextBox, POSTPAID_VALID_RECHARGE_AMOUNT);
        eyes.checkWindow("Entered Valid Recharge Amount");

        driver.findElement(By.xpath("//div[text()='Pay']")).click();
        waitFor(3);
        eyes.check("Payment Options Page", Target.window().fully());
    }

    @Test (alwaysRun = true)
    void postPaidInvalidRechargeTest() {
        driver.get("https://www.jio.com/");
        eyes.checkWindow("Jio Home Page");

        waitFor(3);
        By jionumberTextBox = By.xpath("//input[@data-testid='JDSInput-input']");
        scrollTillElementIntoView(driver, jionumberTextBox);

        typeInTextBox(driver, jionumberTextBox, POSTPAID_PHONE_NUMBER);
        eyes.checkWindow("Entered Mobile Number");

        waitTillElementIsClickable(driver, By.xpath("//div[text()='Proceed']")).click();
        waitFor(3);

        By amountTextBox = By.xpath("//input[@placeholder='Amount']");
        waitTillElementIsPresent(driver, amountTextBox);
        typeInTextBox(driver, amountTextBox, POSTPAID_INVALID_RECHARGE_AMOUNT);
        eyes.checkWindow("Entered Valid Recharge Amount");

        driver.findElement(By.xpath("//div[text()='Pay']")).click();
        waitFor(3);
        eyes.check("Invalid recharge amount error", Target.window().fully());

//        String actualErrorMessage = driver.findElement(By.xpath("//span[@class=\"j-error-message\"]")).getText();
//        String expectedErrorMessage = "Based on your current outstanding amount, maximum payment allowed is Rs." + POSTPAID_VALID_RECHARGE_AMOUNT + ".0";
//        Assert.assertEquals(actualErrorMessage, expectedErrorMessage, "Error message mismatch!");
    }

    @Test (alwaysRun = true)
    void prePaidRechargeTest() {
        driver.get("https://www.jio.com/");
        eyes.checkWindow("Jio Home Page");

        waitFor(3);
        By jionumberTextBox = By.xpath("//input[@data-testid='JDSInput-input']");
        scrollTillElementIntoView(driver, jionumberTextBox);

        typeInTextBox(driver, jionumberTextBox, PREPAID_PHONE_NUMBER);
        eyes.checkWindow("Entered Mobile Number");

        waitTillElementIsClickable(driver, By.xpath("//div[text()='Proceed']")).click();
        waitFor(5);

        eyes.checkWindow("Recharge Options Page");
    }

    private static void typeInTextBox(WebDriver driver, By locator, String inputText) {
        WebElement mobileNumberInput = waitTillElementIsVisible(driver, locator);

        for (char digit : inputText.toCharArray()) {
            mobileNumberInput.sendKeys(Character.toString(digit));
        }
    }
}
