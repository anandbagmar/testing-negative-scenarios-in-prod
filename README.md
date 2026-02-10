# testing-negative-scenarios-in-prod

## Overview

This repository demonstrates how to validate **both positive and negative end-to-end scenarios against a deployed application**  by selectively stubbing specific backend APIs, while allowing the rest of the application traffic to hit the live environment, using a controlled and safe testing approach.

It combines:
- **Specmatic** for intelligent, spec-compliant API stubbing
- **Web automation**: for functional automation using Selenium-Java + Specmatic intelligent stubbing + Applitools Eyes (UFG)
- **Mobile automation**: for functional automation Appium (Android) + Specmatic intelligent stubbing + Applitools Eyes
- **Applitools Visual AI** for visual and UI regression testing

> Specmatic proxy/stub server is the single recommended setup for executing mocked scenarios.

## Table of Contents

* [How the tests work](#how-the-tests-work)
* [Test framework setup](#test-framework-setup)
* [Setup Specmatic stub server (required)](#setup-specmatic-stub-server-required)
* [Test the Web front-end](#test-the-web-front-end)
* [Test the Android app](#test-the-android-app)
* [Applitools Visual AI Setup](./Applitools-README.md)
* [Troubleshooting](./Troubleshooting-README.md)


---

## How the tests work

- The tests run against a mocked recharge experience powered by a Specmatic proxy/stub server.
- For web tests, point to the Specmatic server - http://localhost:8080
- For mobile tests, this repo uses pre-built sample apps from the companion repository **MockedE2EDemo**:
    - `MockedE2EDemo-debug.apk`
    - `MockedE2EDemo-debug.app`

---

## Test Framework Setup

### Prerequisites

#### System requirements
- Java 17+ (for Selenium/Appium test execution)
- Node.js (for Android test dependencies)
- Android Studio + Android SDK (for running emulator)
- (Optional) Xcode (only needed if you want to build iOS sample app from source)


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


## Test the Web front-end (Selenium + Specmatic + Applitools Eyes UFG)

---

#### Run from command line (Gradle wrapper)
```bash
   IS_VISUAL=true PLATFORM=web ./gradlew run
```

---
## Test the Android app (Appium + Specmatic + Applitools Eyes)

### Prerequisites
- The demo app binary exists in `sampleApps/` (copied from the `MockedE2EDemo` repo):
    - `sampleApps/MockedE2EDemo-debug.apk`
- Android emulator/device is running

### Android test pre-step (Node dependencies)

Before running Android Appium tests, run:

```bash
    npm install
```

### Running the tests
```bash
    IS_VISUAL=true PLATFORM=web ./gradlew run
```