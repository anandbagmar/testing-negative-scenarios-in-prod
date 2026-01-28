package com.eot.e2e.negative;

import com.applitools.eyes.*;
import com.applitools.eyes.selenium.BrowserType;
import com.applitools.eyes.selenium.Configuration;
import com.applitools.eyes.selenium.Eyes;
import com.applitools.eyes.selenium.fluent.Target;
import com.applitools.eyes.visualgrid.services.RunnerOptions;
import com.applitools.eyes.visualgrid.services.VisualGridRunner;
import com.eot.utilities.Browser;
import com.eot.utilities.Driver;
import com.znsio.teswiz.tools.Wait;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.annotations.*;

import java.io.File;
import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.eot.e2e.negative.TestData.*;
import static com.eot.utilities.EyesResults.displayVisualValidationResults;
import static com.eot.utilities.StringUtils.getEnvBoolean;
import static com.eot.utilities.Wait.*;

public class JioRechargeWebTest {

    private static final String appName = JioRechargeWebTest.class.getSimpleName();
    private static final String userName = System.getProperty("user.name");
    private static final String APPLITOOLS_API_KEY = System.getenv("APPLITOOLS_API_KEY");
    private static VisualGridRunner visualGridRunner;
    private static BatchInfo batch;
    private Eyes eyes;
    private WebDriver driver;
    private static final boolean USE_UFG = true;
    private static final boolean DISABLE_EYES = getEnvBoolean("DISABLE_EYES", true);

    private static final String URL = "http://localhost:8080";

    @BeforeSuite
    public static void beforeSuite() {
        System.out.println("BeforeSuite");
        visualGridRunner = new VisualGridRunner(new RunnerOptions().testConcurrency(10));
        visualGridRunner.setDontCloseBatches(true);
        batch = new BatchInfo(appName);
        batch.setNotifyOnCompletion(false);
        batch.setSequenceName(JioRechargeWebTest.class.getSimpleName());
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
        config.addBrowser(1920, 1600, BrowserType.FIREFOX);
        config.addBrowser(1600, 1200, BrowserType.EDGE_CHROMIUM);
        config.addBrowser(1920, 1600, BrowserType.SAFARI);
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

    @Test(alwaysRun = true)
    void invalidJioNumberTest() {
        driver.get(URL);
        eyes.check("Jio Home Page", Target.window().fully().ignore(By.xpath("//div[@class=\"slick-center-mode\"]")));

        Wait.waitFor(3);
        By jionumberTextBox = By.xpath("//input[@data-testid='JDSInput-input']");
        By rechargeOrPayBillsHeading = By.xpath("//div[text()='Recharge or pay bills']");
        scrollTillElementIntoView(driver, rechargeOrPayBillsHeading);

        typeInTextBox(driver, jionumberTextBox, INVALID_PHONE_NUMBER);
        eyes.check("Entered Mobile Number", Target.region(By.xpath("//div[@class=\"recharge-paybill-withleads\"]")));

        waitTillElementIsClickable(driver, By.xpath("//div[text()='Proceed']")).click();
        Wait.waitFor(2);
        eyes.check("Invalid Jio Number entered", Target.window().fully());
    }

    @Test(alwaysRun = true)
    void postPaidValidRechargeTest() {
        driver.get(URL);
        eyes.check("Jio Home Page", Target.window().fully().ignore(By.xpath("//div[@class=\"slick-center-mode\"]")));

        Wait.waitFor(3);
        By jionumberTextBox = By.xpath("//input[@data-testid='JDSInput-input']");
        By rechargeOrPayBillsHeading = By.xpath("//div[text()='Recharge or pay bills']");
        scrollTillElementIntoView(driver, rechargeOrPayBillsHeading);

        typeInTextBox(driver, jionumberTextBox, POSTPAID_VALID_RECHARGE_PHONE_NUMBER);
        eyes.check("Entered Mobile Number", Target.region(By.xpath("//div[@class=\"recharge-paybill-withleads\"]")));

        waitTillElementIsClickable(driver, By.xpath("//div[text()='Proceed']")).click();
        Wait.waitFor(3);

        By amountTextBox = By.xpath("//input[@placeholder='Amount']");
        waitTillElementIsPresent(driver, amountTextBox);
        typeInTextBox(driver, amountTextBox, POSTPAID_VALID_RECHARGE_AMOUNT);
        eyes.checkWindow("Entered Valid Recharge Amount");

        System.out.println("Clicking Pay button");
        driver.findElement(By.xpath("//div[text()='Pay']")).click();
        Wait.waitFor(3);
        System.out.println("Checking Payment Options page");
        eyes.check("Payment Options Page", Target.window().fully());
    }

    @Test(alwaysRun = true)
    void postPaidInvalidRechargeTest() {
        driver.get(URL);
        eyes.check("Jio Home Page", Target.window().fully().ignore(By.xpath("//div[@class=\"slick-center-mode\"]")));

        Wait.waitFor(3);
        By jionumberTextBox = By.xpath("//input[@data-testid='JDSInput-input']");
        By rechargeOrPayBillsHeading = By.xpath("//div[text()='Recharge or pay bills']");
        scrollTillElementIntoView(driver, rechargeOrPayBillsHeading);

        typeInTextBox(driver, jionumberTextBox, POSTPAID_INVALID_RECHARGE_PHONE_NUMBER);
        eyes.check("Entered Mobile Number", Target.region(By.xpath("//div[@class=\"recharge-paybill-withleads\"]")));

        System.out.println("Clicking Proceed button");
        waitTillElementIsClickable(driver, By.xpath("//div[text()='Proceed']")).click();
        Wait.waitFor(3);
        System.out.println("Enter recharge amount");

        By amountTextBox = By.xpath("//input[@placeholder='Amount']");
        waitTillElementIsPresent(driver, amountTextBox);
        typeInTextBox(driver, amountTextBox, POSTPAID_INVALID_RECHARGE_AMOUNT);
        eyes.check("Entered Valid Recharge Amount", Target.window().fully().ignore(By.xpath("//div[@class=\"slick-center-mode\"]")));

        driver.findElement(By.xpath("//div[text()='Pay']")).click();
        Wait.waitFor(3);
        System.out.println("Checking for Invalid recharge amount error");
        eyes.check("Invalid recharge amount error", Target.window().fully().ignore(By.xpath("//div[@class=\"slick-center-mode\"]")));

        //        String actualErrorMessage = driver.findElement(By.xpath("//span[@class=\"j-error-message\"]")).getText();
        //        String expectedErrorMessage = "Based on your current outstanding amount, maximum payment allowed is Rs." + POSTPAID_VALID_RECHARGE_AMOUNT + ".0";
        //        Assert.assertEquals(actualErrorMessage, expectedErrorMessage, "Error message mismatch!");
    }

    @Test(alwaysRun = true)
    void prePaidRecharge0PlansTest() {
        prepaidRechargePlanTest(PREPAID_PHONE_NUMBER_0_PLANS);
    }

    @Test(alwaysRun = true)
    void prePaidRecharge1PlanTest() {
        prepaidRechargePlanTest(PREPAID_PHONE_NUMBER_1_PLAN);
    }

    @Test(alwaysRun = true)
    void prePaidRecharge2PlansTest() {
        prepaidRechargePlanTest(PREPAID_PHONE_NUMBER_2_PLANS);
    }

    @Test(alwaysRun = true)
    void prePaidRecharge5PlansTest() {
        prepaidRechargePlanTest(PREPAID_PHONE_NUMBER_5_PLANS);
    }

    @Test(alwaysRun = true)
    void prePaidRecharge10PlansTest() {
        prepaidRechargePlanTest(PREPAID_PHONE_NUMBER_10_PLANS);
    }

    private void prepaidRechargePlanTest(String prepaidPhoneNumber10Plans) {
        driver.get(URL);
        eyes.check("Jio Home Page", Target.window().fully().ignore(By.xpath("//div[@class=\"slick-center-mode\"]")));

        Wait.waitFor(3);
        By jionumberTextBox = By.xpath("//input[@data-testid='JDSInput-input']");
        By rechargeOrPayBillsHeading = By.xpath("//div[text()='Recharge or pay bills']");
        scrollTillElementIntoView(driver, rechargeOrPayBillsHeading);

        typeInTextBox(driver, jionumberTextBox, prepaidPhoneNumber10Plans);
        eyes.check("Entered Mobile Number", Target.region(By.xpath("//div[@class=\"recharge-paybill-withleads\"]")));

        Wait.waitFor(2);
        driver.findElement(By.xpath("//div[text()='Proceed']")).click();
        Wait.waitFor(5);

        eyes.checkWindow("Recharge Options Page");
    }

    private static void typeInTextBox(WebDriver driver, By locator, String inputText) {
        System.out.println("Typing text: " + inputText + " into element: " + locator.toString());
        WebElement mobileNumberInput = waitTillElementIsVisible(driver, locator);

        for (char digit : inputText.toCharArray()) {
            mobileNumberInput.sendKeys(Character.toString(digit));
        }
        Wait.waitFor(1);
        System.out.println("Typed text: " + inputText + " into element: " + locator.toString());
    }
}
