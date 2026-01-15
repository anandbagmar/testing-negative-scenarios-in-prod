# testing-negative-scenarios-in-prod

## Overview

This repository demonstrates how to validate **both positive and negative end-to-end scenarios against a deployed application**  by selectively stubbing specific backend APIs, while allowing the rest of the application traffic to hit the live environment, using a controlled and safe testing approach.

It combines:
- **Web automation**: for functional automation using Selenium-Java + Specmatic intelligent stubbing + Applitools Eyes (UFG)
- **Mobile automation**: for functional automation Appium (Android) + Specmatic intelligent stubbing + Applitools Eyes
- **Applitools Visual AI** for visual and UI regression testing
- **Specmatic** for intelligent, spec-compliant API stubbing

> Specmatic proxy/stub server is the single recommended setup for executing mocked scenarios.

---

## How the tests work

- The tests run against a mocked recharge experience powered by a Specmatic proxy/stub server.
- For web tests, point to the Specmatic server - http://localhost:8080
- For mobile tests, this repo uses pre-built sample apps from the companion repository **MockedE2EDemo**:
    - `MockedE2EDemo-debug.apk`
    - `MockedE2EDemo-debug.app`

---

## Prerequisites

### System requirements
- Java 17+ (for Selenium/Appium test execution)
- Node.js (for Android test dependencies)
- Android Studio + Android SDK (for running emulator)
- (Optional) Xcode (only needed if you want to build iOS sample app from source)

---

## Setup (fresh clone)

### 1) Clone this repo

```bash

git clone https://github.com/anandbagmar/testing-negative-scenarios-in-prod/
cd testing-negative-scenarios-in-prod
```

### 2) Build sample apps (from MockedE2EDemo)

The Android (and iOS) sample apps used by the tests are generated from:

- Repo: https://github.com/anandbagmar/MockedE2EDemo
- Build instructions: https://github.com/anandbagmar/MockedE2EDemo/blob/main/README-IndependentBuild.md

Follow the instructions in that README to generate:
- `MockedE2EDemo-debug.apk`
- `MockedE2EDemo-debug.app`

### 3) Copy Android app into this repo
Copy the generated APK from the MockedE2EDemo repo into this repo at:

```text
testing-negative-scenarios-in-prod/sampleApps/
```

Example:
```bash

cp /path/to/MockedE2EDemo/android/app/build/outputs/apk/debug/MockedE2EDemo-debug.apk ./sampleApps/
```

---

## Applitools Visual AI Setup

To run tests with Applitools Visual AI:

1. Sign up for a <a href="https://auth.applitools.com/users/general-register?app=eyes" target="_blank" rel="noopener noreferrer">
   Applitools trial account
   </a>

2. Obtain your **APPLITOOLS_API_KEY**

3. Configure the API key using one of the following options:

### Option A: Environment Variable (Recommended)
```bash

export APPLITOOLS_API_KEY=<your_api_key>
```

### Option B: Hardcode in Test (Not recommended for CI)
Update the following line in  
`src/test/java/com/eot/e2e/negative/JioRecharge_UFG_Test.java`

```java
eyes.setApiKey(System.getenv("APPLITOOLS_API_KEY"));
```

Replace with:
```java
eyes.setApiKey("<replace_me>");
```

---

## Enable/Disable Applitools validation (DISABLE_EYES)

By default, Applitools validations are **skipped**.

- Default: `DISABLE_EYES=true` (Eyes validations will be skipped)
- To enable Applitools validations: set `DISABLE_EYES=false`

### Disable Eyes (default)
```bash

export DISABLE_EYES=true
```

### Enable Eyes validations
```bash

export DISABLE_EYES=false
```

## Start Specmatic stub server (required)

Before running any web or Android tests, setup and start the Specmatic proxy/stub server using:

- https://github.com/anandbagmar/testing-negative-scenarios-in-prod/blob/main/README-Specmatic-Stub-Setup.md

This will:
- start the Specmatic proxy server (default on port 8080)
- load stub recordings from `lib/proxy_recordings_examples`
- allow Android emulator to access the proxy via ADB reverse

---

## Android test pre-step (Node dependencies)

Before running Android Appium tests, run:

```bash

npm install
```

---

## Running the tests (Web + Android)

> Before running **any** tests, make sure the **Specmatic stub/proxy** is running by following the [Specmatic Stub Setup](README-Specmatic-Stub-Setup.md).

---

### Web E2E test (Selenium + Specmatic + Applitools Eyes UFG)

Test class:
- `src/test/java/com/eot/e2e/negative/JioRecharge_UFG_Test.java`

#### Run from command line (Gradle wrapper)
```bash
  ./gradlew test --tests "com.eot.e2e.negative.JioRecharge_UFG_Test"
```

#### Run from IDE (IntelliJ IDEA / Android Studio)
1. Open `JioRecharge_UFG_Test.java`
2. Click the green ▶︎ gutter icon next to the class (or a test method)
3. Make sure your environment variables are set in the Run Configuration:
    - `DISABLE_EYES=true` (default; skips Eyes)
    - `APPLITOOLS_API_KEY` (required only if you set `DISABLE_EYES=false`)

---

### Android E2E test (Appium + Specmatic + Applitools Eyes)

Test class:
- `src/test/java/com/eot/e2e/negative/JioRechargeAndroidTest.java`

#### Prerequisites
- The demo app binary exists in `sampleApps/` (copied from the `MockedE2EDemo` repo):
    - `sampleApps/MockedE2EDemo-debug.apk`
- Android emulator/device is running
- `adb reverse` is configured (see [Specmatic Stub Setup](README-Specmatic-Stub-Setup.md))
- Appium server is running, for example:
  ```bash
  appium --base-path /wd/hub --log-level info
  ```

#### Run from command line (Gradle wrapper)
```bash
  ./gradlew test --tests "com.eot.e2e.negative.JioRechargeAndroidTest"
```

#### Run from IDE (IntelliJ IDEA / Android Studio)
1. Open `JioRechargeAndroidTest.java`
2. Run the class using the ▶︎ gutter icon
3. In your Run Configuration, set:
    - `DISABLE_EYES=true` (default) or `DISABLE_EYES=false`
    - `APPLITOOLS_API_KEY` (only needed when `DISABLE_EYES=false`)

---

## Troubleshooting

### Emulator cannot access localhost:8080
Ensure:
```bash

adb reverse tcp:8080 tcp:8080
```

### SampleApps missing
Ensure you have copied `MockedE2EDemo-debug.apk` into:
```text
sampleApps/
```
