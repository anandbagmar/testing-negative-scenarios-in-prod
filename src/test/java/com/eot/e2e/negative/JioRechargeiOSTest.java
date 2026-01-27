package com.eot.e2e.negative;

import com.applitools.eyes.*;
import com.applitools.eyes.appium.Eyes;
import com.eot.utilities.IOSCommands;
import com.znsio.teswiz.tools.Wait;
import io.appium.java_client.AppiumBy;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.ios.options.XCUITestOptions;
import io.appium.java_client.service.local.AppiumDriverLocalService;
import io.appium.java_client.service.local.AppiumServiceBuilder;
import io.appium.java_client.service.local.flags.GeneralServerFlag;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.*;

import java.io.File;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.util.Date;
import java.util.Set;

import static com.eot.utilities.StringUtils.getEnvBoolean;
import static com.eot.e2e.negative.TestData.*;
import static com.eot.utilities.Wait.*;

public class JioRechargeiOSTest {
    private static final String className = JioRechargeiOSTest.class.getSimpleName();
    private static final long epochSecond = new Date().toInstant().getEpochSecond();
    private static final String userName = System.getProperty("user.name");
    private static final boolean IS_FULL_RESET = false;
    private static final String PLATFORM_NAME = "iOS";
    private static final boolean IS_MULTI_DEVICE = false;
    private static BatchInfo batch;
    private static String APPIUM_SERVER_URL = "http://localhost:4723/wd/hub/";
    private static AppiumDriverLocalService localAppiumServer;
    private static String DEBUG_APP_NAME = "sampleApps" + File.separator + "MockedE2EDemo-debug.app";
    private static String APP_NAME = DEBUG_APP_NAME;
    private static String APK_WITH_NML_NAME = "sampleApps" + File.separator + "dist" + File.separator + "MockedE2EDemo-debug.app";
    private static final boolean DISABLE_EYES = getEnvBoolean("DISABLE_EYES", true);
    private static boolean IS_NML = false;
    private final String APPLITOOLS_API_KEY = System.getenv("APPLITOOLS_API_KEY");
    private IOSDriver driver;
    private Eyes eyes;
    private final static String DEVICE_UUID = "1158D906-D411-447A-8BF8-F22B2333827F";
    private final static String DEVICE_NAME = "iPhone 14 Pro Max";
    private final static String BUNDLE_ID = "io.specmatic.e2edemo";


    @BeforeSuite
    static void beforeAll() {
        startAppiumServer();
        String batchName = className;
        batch = new BatchInfo(batchName);
        batch.setId(String.valueOf(epochSecond));
        batch.addProperty("REPOSITORY_NAME", new File(System.getProperty("user.dir")).getName());
        batch.addProperty("IS_NML", String.valueOf(IS_NML));
        batch.addProperty("IS_MULTI_DEVICE", String.valueOf(IS_MULTI_DEVICE));
        System.out.println("Create AppiumRunner");
        System.out.printf("Batch name: %s%n", batch.getName());
        System.out.printf("Batch startedAt: %s%n", batch.getStartedAt().getTime());
        System.out.printf("Batch BatchId: %s%n", batch.getId());
    }

    @AfterSuite
    static void afterAll() {
        System.out.printf("AfterAll: Stopping the local Appium server running on: '%s'%n", APPIUM_SERVER_URL);
        if (null != batch) {
            batch.setCompleted(true);
        }
        if (null != localAppiumServer) {
            localAppiumServer.stop();
            System.out.printf("Is Appium server running? %s%n", localAppiumServer.isRunning());
        }
    }

    private static void startAppiumServer() {
        System.out.println("Start local Appium server");
        AppiumServiceBuilder serviceBuilder = new AppiumServiceBuilder();
        // Use any port, in case the default 4723 is already taken (maybe by another Appium server)
        serviceBuilder.usingAnyFreePort();
        serviceBuilder.withAppiumJS(new File("./node_modules/appium/build/lib/main.js"));
        serviceBuilder.withLogFile(new File(System.getenv("LOG_DIR") + "/appium_logs.txt"));
        serviceBuilder.withArgument(GeneralServerFlag.ALLOW_INSECURE, "adb_shell");
        serviceBuilder.withArgument(GeneralServerFlag.RELAXED_SECURITY);

        // Appium 2.x
        localAppiumServer = AppiumDriverLocalService.buildService(serviceBuilder);

        localAppiumServer.start();
        APPIUM_SERVER_URL = localAppiumServer.getUrl().toString();
        System.out.printf("Appium server started on url: '%s'%n", localAppiumServer.getUrl().toString());
    }

    @BeforeMethod
    public void beforeEach(Method testInfo) {
        System.out.printf("Test: %s - BeforeEach%n", testInfo.getName());
        setUpiOS(testInfo);
    }

    @AfterMethod
    void tearDown(Method testInfo) {
        System.out.println("AfterEach: Test - " + testInfo.getName());
        boolean isPass = true;
        if (!DISABLE_EYES) {
            TestResults testResults = eyes.close(false);
            System.out.printf("Test: %s\n%s%n", testResults.getName(), testResults);
            if (testResults.getStatus().equals(TestResultsStatus.Failed) || testResults.getStatus().equals(TestResultsStatus.Unresolved)) {
                isPass = false;
            }
        }
        if (null != driver) {
            driver.quit();
        }
        if (!isPass) {
            System.out.printf("Test: %s had visual differences.%n", testInfo.getName());
        }
    }

    void setUpiOS(Method testInfo) {
        System.out.println("BeforeEach: Test - " + testInfo.getName());
        System.out.printf("Create AppiumDriver for iOS test - %s%n", APPIUM_SERVER_URL);

        IOSCommands.bootSimIfNeeded(DEVICE_UUID);
        IOSCommands.grantLocation(DEVICE_UUID, BUNDLE_ID);

        // optional but helpful if web content expects a location
        IOSCommands.setSimLocation(DEVICE_UUID, 19.0760, 72.8777); // Mumbai

        XCUITestOptions xcuiTestOptions = new XCUITestOptions();
        xcuiTestOptions.setPlatformName(PLATFORM_NAME);
        xcuiTestOptions.setUdid(DEVICE_UUID);
        xcuiTestOptions.setCapability(XCUITestOptions.DEVICE_NAME_OPTION, DEVICE_NAME);

        xcuiTestOptions.setCapability(XCUITestOptions.AUTOMATION_NAME_OPTION, "XCUITest");
        xcuiTestOptions.setCapability(XCUITestOptions.NEW_COMMAND_TIMEOUT_OPTION, 15000);
        xcuiTestOptions.setCapability(XCUITestOptions.AUTO_ACCEPT_ALERTS_OPTION, true);
        xcuiTestOptions.setCapability(XCUITestOptions.PRINT_PAGE_SOURCE_ON_FIND_FAILURE_OPTION, true);
        xcuiTestOptions.setCapability(XCUITestOptions.INCLUDE_SAFARI_IN_WEBVIEWS_OPTION, true);
//        xcuiTestOptions.setCapability(XCUITestOptions.WEBVIEW_CONNECT_TIMEOUT_OPTION, "120000");
//        xcuiTestOptions.setCapability(XCUITestOptions.WEBKIT_RESPONSE_TIMEOUT_OPTION, "120000");
        xcuiTestOptions.setCapability(XCUITestOptions.NATIVE_WEB_TAP_OPTION, true);
//        xcuiTestOptions.setCapability(XCUITestOptions.SHOW_XCODE_LOG_OPTION, true);
        xcuiTestOptions.setCapability(XCUITestOptions.FULL_RESET_OPTION, IS_FULL_RESET);
        xcuiTestOptions.setCapability(XCUITestOptions.NO_RESET_OPTION, false);
        xcuiTestOptions.setCapability("appium:autoGrantPermissions", true);
        xcuiTestOptions.setCapability("appium:autoDismissAlerts", false);
        xcuiTestOptions.setCapability("appium:locationServicesEnabled", true);
        xcuiTestOptions.setCapability("appium:locationServicesAuthorized", true);
        if (IS_NML) {
            xcuiTestOptions.setCapability(XCUITestOptions.APP_OPTION, new File(APK_WITH_NML_NAME).getAbsolutePath());
            System.out.printf("Add devices to NML configuration using capabilities: %%n%s%n", xcuiTestOptions);
            Eyes.setMobileCapabilities(xcuiTestOptions, APPLITOOLS_API_KEY);
        } else {
            String absolutePath = new File(APP_NAME).getAbsolutePath();
            System.out.println("APK absolute path: " + absolutePath);
            xcuiTestOptions.setCapability(XCUITestOptions.APP_OPTION, absolutePath);
        }
        System.out.println("UiAutomator2Options:");
        for (String capabilityName : xcuiTestOptions.getCapabilityNames()) {
            System.out.println("\t" + capabilityName + ": " + xcuiTestOptions.getCapability(capabilityName));
        }

        try {
            driver = new IOSDriver(new URL(APPIUM_SERVER_URL), xcuiTestOptions);
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(1L));
        } catch (MalformedURLException e) {
            System.err.println("Error creating Appium driver for android device with capabilities: " + xcuiTestOptions);
            throw new RuntimeException(e);
        }
        System.out.printf("Created AppiumDriver for - %s%n", APPIUM_SERVER_URL);
        configureEyes(testInfo);
    }

    private void configureEyes(Method testInfo) {
        if (!DISABLE_EYES) {
            System.out.println("Eyes is enabled for this test.");
        } else {
            System.out.println("Eyes is disabled for this test.");
        }
        System.out.println("Setup Eyes configuration");
        eyes = new Eyes();

        eyes.setLogHandler(new StdoutLogHandler(true));
        eyes.setBatch(batch);
        eyes.setEnvName("pre-prod");
        eyes.setBranchName("main");
        eyes.setEnvName("prod");
        eyes.addProperty("username", userName);
        eyes.setApiKey(APPLITOOLS_API_KEY);
        eyes.setServerUrl("https://eyes.applitools.com");
        eyes.setMatchLevel(MatchLevel.STRICT);
        eyes.setIsDisabled(DISABLE_EYES);
//        eyes.setIgnoreCaret(true);
//        eyes.setIgnoreDisplacements(true);
//        eyes.setForceFullPageScreenshot(false);
//        eyes.setSaveNewTests(true);
        if (IS_NML && IS_MULTI_DEVICE) {
            //            eyes.setConfiguration(eyes.getConfiguration().addMobileDevice(new AndroidDeviceInfo(AndroidDeviceName.Galaxy_S10_Plus)));
            //            eyes.setConfiguration(eyes.getConfiguration().addMobileDevice(new AndroidDeviceInfo(AndroidDeviceName.Galaxy_S21)));
        }
        eyes.open(driver, className, testInfo.getName());
    }

    private static void switchToWebView(IOSDriver driver) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));


        wait.until(d -> {
            Set<String> contexts = driver.getContextHandles();
            System.out.println("Available contexts: " + contexts);
            System.out.println("Current context before switch to webview: " + driver.getContext());
            return contexts.stream().anyMatch(c -> c.toUpperCase().contains("WEBVIEW"));
        });

        for (String context : driver.getContextHandles()) {
            if (context.toUpperCase().contains("WEBVIEW")) {
                driver.context(context);
                System.out.println("✅ Switched to context: " + context);
                return;
            }
        }
        System.out.println("Current context (after switch): " + driver.getContext());
        throw new RuntimeException("❌ No WEBVIEW context found even after waiting.");
    }

    private static void switchBackToNative(IOSDriver driver) {
        driver.context("NATIVE_APP");
        System.out.println("✅ Switched back to native context");
    }

    @Test(alwaysRun = true)
    void invalidJioNumberTest() {
        switchToRechargePhoneNumber();
        typeInTextBox(driver, AppiumBy.xpath("//input[@placeholder=\"Jio Number\"]"), INVALID_PHONE_NUMBER);
        eyes.checkWindow("Entered Mobile Number");
        driver.findElement(AppiumBy.xpath("//div[text()=\"Proceed\"]")).click();
        eyes.checkWindow("Invalid Jio Number entered");
    }

    @Test (alwaysRun = true)
    void postPaidValidRechargeTest() {
        switchToRechargePhoneNumber();

        By jionumberTextBox = By.xpath("//input[@data-testid='JDSInput-input']");
        By rechargeOrPayBillsHeading = By.xpath("//div[text()='Recharge or pay bills']");
        scrollTillElementIntoView(driver, rechargeOrPayBillsHeading);

        typeInTextBox(driver, jionumberTextBox, POSTPAID_VALID_RECHARGE_PHONE_NUMBER);
        eyes.checkWindow("Entered Mobile Number");

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
        eyes.checkWindow("Payment Options Page");
    }

    @Test (alwaysRun = true)
    void postPaidInvalidRechargeTest() {
        switchToRechargePhoneNumber();

        By jionumberTextBox = By.xpath("//input[@data-testid='JDSInput-input']");
        By rechargeOrPayBillsHeading = By.xpath("//div[text()='Recharge or pay bills']");
        scrollTillElementIntoView(driver, rechargeOrPayBillsHeading);

        typeInTextBox(driver, jionumberTextBox, POSTPAID_INVALID_RECHARGE_PHONE_NUMBER);
        eyes.checkWindow("Entered Mobile Number");

        System.out.println("Clicking Proceed button");
        waitTillElementIsClickable(driver, By.xpath("//div[text()='Proceed']")).click();
        Wait.waitFor(3);
        System.out.println("Enter recharge amount");

        By amountTextBox = By.xpath("//input[@placeholder='Amount']");
        waitTillElementIsPresent(driver, amountTextBox);
        typeInTextBox(driver, amountTextBox, POSTPAID_INVALID_RECHARGE_AMOUNT);
        eyes.checkWindow("Entered Valid Recharge Amount");

        driver.findElement(By.xpath("//div[text()='Pay']")).click();
        Wait.waitFor(3);
        System.out.println("Checking for Invalid recharge amount error");
        eyes.checkWindow("Invalid recharge amount error");
    }

    @Test(alwaysRun = true)
    void prePaidRecharge0PlansTest() {
        prepaidRechargePlanTest(PREPAID_PHONE_NUMBER_0_PLANS);
    }

    @Test (alwaysRun = true)
    void prePaidRecharge1PlanTest() {
        prepaidRechargePlanTest(PREPAID_PHONE_NUMBER_1_PLAN);
    }

    @Test (alwaysRun = true)
    void prePaidRecharge2PlansTest() {
        prepaidRechargePlanTest(PREPAID_PHONE_NUMBER_2_PLANS);
    }

    @Test (alwaysRun = true)
    void prePaidRecharge5PlansTest() {
        prepaidRechargePlanTest(PREPAID_PHONE_NUMBER_5_PLANS);
    }

    @Test (alwaysRun = true)
    void prePaidRecharge10PlansTest() {
        prepaidRechargePlanTest(PREPAID_PHONE_NUMBER_10_PLANS);
    }

    private void switchToRechargePhoneNumber() {
        Wait.waitFor(3);
        eyes.checkWindow("Specmatic Android E2E Demo App");
        Wait.waitFor(2);
        driver.findElement(AppiumBy.accessibilityId("home.btn.recharge")).click();
        Wait.waitFor(10);
        switchToWebView(driver);
        Wait.waitFor(2);
        eyes.checkWindow("Jio Home Page");
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

    private void prepaidRechargePlanTest(String prepaidPhoneNumber) {
        switchToRechargePhoneNumber();
        typeInTextBox(driver, AppiumBy.xpath("//input[@placeholder=\"Jio Number\"]"), prepaidPhoneNumber);
        driver.findElement(AppiumBy.xpath("//div[text()=\"Proceed\"]")).click();
        eyes.checkWindow("Recharge Options Page");
        if (DISABLE_EYES) {
            Wait.waitFor(15);
        }
    }

}
