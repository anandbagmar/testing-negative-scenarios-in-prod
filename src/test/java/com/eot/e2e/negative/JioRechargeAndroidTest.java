package com.eot.e2e.negative;

import com.applitools.eyes.*;
import com.applitools.eyes.appium.Eyes;
import com.eot.utilities.AndroidCommands;
import com.eot.utilities.ShellUtils;
import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
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

public class JioRechargeAndroidTest {
    private static final String className = JioRechargeAndroidTest.class.getSimpleName();
    private static final long epochSecond = new Date().toInstant().getEpochSecond();
    private static final String userName = System.getProperty("user.name");
    private static final boolean IS_FULL_RESET = true;
    private static final String PLATFORM_NAME = "android";
    private static final boolean IS_MULTI_DEVICE = false;
    private static BatchInfo batch;
    private static String APPIUM_SERVER_URL = "http://localhost:4723/wd/hub/";
    private static AppiumDriverLocalService localAppiumServer;
    private static String DEBUG_APK_NAME = "sampleApps" + File.separator + "MockedE2EDemo-debug.apk";
    private static String APK_NAME = DEBUG_APK_NAME;
    private static String APK_WITH_NML_NAME = "sampleApps" + File.separator + "dist" + File.separator + "MockedE2EDemo-debug.apk";
    private static final boolean DISABLE_EYES = getEnvBoolean("DISABLE_EYES", true);
    private static boolean IS_NML = false;
    private final String APPLITOOLS_API_KEY = System.getenv("APPLITOOLS_API_KEY");
    private AndroidDriver driver;
    private Eyes eyes;

    @BeforeSuite
    static void beforeAll() {
        AndroidCommands.adbReverse8080();
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
        setUpAndroid(testInfo);
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

    void setUpAndroid(Method testInfo) {
        System.out.println("BeforeEach: Test - " + testInfo.getName());
        System.out.printf("Create AppiumDriver for android test - %s%n", APPIUM_SERVER_URL);
        UiAutomator2Options uiAutomator2Options = new UiAutomator2Options();
        uiAutomator2Options.setPlatformName(PLATFORM_NAME);

        uiAutomator2Options.setCapability(UiAutomator2Options.AUTOMATION_NAME_OPTION, "UiAutomator2");
        uiAutomator2Options.setCapability(UiAutomator2Options.NEW_COMMAND_TIMEOUT_OPTION, 15000);
        uiAutomator2Options.setCapability(UiAutomator2Options.DEVICE_NAME_OPTION, "Android");
        uiAutomator2Options.setCapability(UiAutomator2Options.PRINT_PAGE_SOURCE_ON_FIND_FAILURE_OPTION, true);
        uiAutomator2Options.setCapability(UiAutomator2Options.AUTO_GRANT_PERMISSIONS_OPTION, true);
        uiAutomator2Options.setCapability(UiAutomator2Options.FULL_RESET_OPTION, IS_FULL_RESET);
        if (IS_NML) {
            uiAutomator2Options.setCapability(UiAutomator2Options.APP_OPTION, new File(APK_WITH_NML_NAME).getAbsolutePath());
            System.out.printf("Add devices to NML configuration using capabilities: %%n%s%n", uiAutomator2Options);
            Eyes.setMobileCapabilities(uiAutomator2Options, APPLITOOLS_API_KEY);
        } else {
            String absolutePath = new File(APK_NAME).getAbsolutePath();
            System.out.println("APK absolute path: " + absolutePath);
            uiAutomator2Options.setCapability(UiAutomator2Options.APP_OPTION, absolutePath);
            uiAutomator2Options.setCapability("appium:app", absolutePath);
        }
        System.out.println("UiAutomator2Options:");
        for (String capabilityName : uiAutomator2Options.getCapabilityNames()) {
            System.out.println("\t" + capabilityName + ": " + uiAutomator2Options.getCapability(capabilityName));
        }

        try {
            driver = new AndroidDriver(new URL(APPIUM_SERVER_URL), uiAutomator2Options);
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(1L));
        } catch (MalformedURLException e) {
            System.err.println("Error creating Appium driver for android device with capabilities: " + uiAutomator2Options);
            throw new RuntimeException(e);
        }
        System.out.printf("Created AppiumDriver for - %s%n", APPIUM_SERVER_URL);
        configureEyes(testInfo);
    }

    private void configureEyes(Method testInfo) {
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
        eyes.setIgnoreCaret(true);
        eyes.setIgnoreDisplacements(true);
        eyes.setForceFullPageScreenshot(false);
        eyes.setSaveNewTests(true);
        if (IS_NML && IS_MULTI_DEVICE) {
            //            eyes.setConfiguration(eyes.getConfiguration().addMobileDevice(new AndroidDeviceInfo(AndroidDeviceName.Galaxy_S10_Plus)));
            //            eyes.setConfiguration(eyes.getConfiguration().addMobileDevice(new AndroidDeviceInfo(AndroidDeviceName.Galaxy_S21)));
        }
        eyes.open(driver, className, testInfo.getName());
    }

    private static void switchToWebView(AndroidDriver driver) {
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

    private static void switchBackToNative(AndroidDriver driver) {
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
        waitFor(3);

        By amountTextBox = By.xpath("//input[@placeholder='Amount']");
        waitTillElementIsPresent(driver, amountTextBox);
        typeInTextBox(driver, amountTextBox, POSTPAID_VALID_RECHARGE_AMOUNT);
        eyes.checkWindow("Entered Valid Recharge Amount");

        System.out.println("Clicking Pay button");
        driver.findElement(By.xpath("//div[text()='Pay']")).click();
        waitFor(3);
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
        waitFor(3);
        System.out.println("Enter recharge amount");

        By amountTextBox = By.xpath("//input[@placeholder='Amount']");
        waitTillElementIsPresent(driver, amountTextBox);
        typeInTextBox(driver, amountTextBox, POSTPAID_INVALID_RECHARGE_AMOUNT);
        eyes.checkWindow("Entered Valid Recharge Amount");

        driver.findElement(By.xpath("//div[text()='Pay']")).click();
        waitFor(3);
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
        waitFor(3);
        eyes.checkWindow("Specmatic Android E2E Demo App");
        waitFor(2);
        driver.findElement(AppiumBy.accessibilityId("home.btn.recharge")).click();
        waitFor(10);
        switchToWebView(driver);
        waitFor(2);
        eyes.checkWindow("Jio Home Page");
    }

    private static void typeInTextBox(WebDriver driver, By locator, String inputText) {
        System.out.println("Typing text: " + inputText + " into element: " + locator.toString());
        WebElement mobileNumberInput = waitTillElementIsVisible(driver, locator);

        for (char digit : inputText.toCharArray()) {
            mobileNumberInput.sendKeys(Character.toString(digit));
        }
        waitFor(1);
        System.out.println("Typed text: " + inputText + " into element: " + locator.toString());
    }

    private void prepaidRechargePlanTest(String prepaidPhoneNumber) {
        switchToRechargePhoneNumber();
        typeInTextBox(driver, AppiumBy.xpath("//input[@placeholder=\"Jio Number\"]"), prepaidPhoneNumber);
        driver.findElement(AppiumBy.xpath("//div[text()=\"Proceed\"]")).click();
        eyes.checkWindow("Recharge Options Page");
    }
}
