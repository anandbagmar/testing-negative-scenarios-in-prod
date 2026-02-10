# Specmatic stub server setup (required for WebView flows)

The mobile apps in this repo load the recharge flow inside a WebView that points to a local endpoint (`http://localhost:8080`).  
To make this work on a freshly cloned repo, you must start the Specmatic proxy/stub server and wire the emulator/device networking.

## Prerequisites

1. Clone the Specmatic recordings repo:

```bash

git clone https://github.com/anandbagmar/testing-negative-scenarios-in-prod/
```

2. Use the latest **Specmatic** version from:

- https://repo.specmatic.io/#/releases/io/specmatic/enterprise/executable-all

Example: `enterprise-all-1.1.2.jar`

Update build.gradle with this latest version.

## Start Specmatic stub/proxy server

The tests will automatically start/stop the proxy server programmatically and will load Specmatic mocks from:

```text
testing-negative-scenarios-in-prod/src/test/resources/specmatic/proxy_recordings_examples/
```

## Android setup

1. Start an Android emulator
2. Setup ADB reverse proxy so the emulator can access your host machine:

```bash

adb reverse tcp:8080 tcp:8080
```

Now `http://localhost:8080` inside the emulator will resolve to `localhost:8080` on your laptop (where Specmatic proxy is running).

This command is executed automatically when running the tests for Android platform.