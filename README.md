This repo contains various (positive and negative) tests implemented using Selenium-java. 
There is integration with Applitools integrated for Visual Testing.
To test the negative flows, the repo uses Specmatic.io for intelligent stubbing of APIs.

# testing-negative-scenarios-in-prod

# Machine setup instructions & Prerequisites
- Install JDK 17 or higher
- Clone this git repo (https://github.com/anandbagmar/testing-negative-scenarios-in-prod) on your laptop
- Open the cloned project in your IDE as a Gradle project. This will automatically download all the dependencies
- Ensure that you have Chrome browser installed on your machine

  - To run the tests with Applitools Visual AI
  Sign up for a free trial account on Applitools (https://applitools.com/users/sign_up) to get your API key.
  Set APPLITOOLS_API_KEY 
    - Set APPLITOOLS_API_KEY as an environment variable, or,
    - Update the APPLITOOLS_API_KEY in the file - [JioRecharge_UFG_Test.java](src/test/java/com/eot/e2e/negative/JioRecharge_UFG_Test.java) as shown below:
        ```
        > eyes.setApiKey(System.getenv("APPLITOOLS_API_KEY"));
        ```
        with
        ```
        > eyes.setApiKey("<replace_me>");
        ```

- To run the tests with Specmatic stubbing
  - **Install & trust all** the certificates required to run the tests with Specmatic stubbing. The certificates are available in the folder - [specmatic-certs](src/test/resources/specmatic/specmatic-certs)
  
  - Download the latest release of Specmatic using the following command: 
    ```shell
    curl -L -f -o ./src/test/resources/specmatic/specmatic.jar https://github.com/specmatic/specmatic/releases/latest/download/specmatic.jar
    ```

  - Start Mock Server
  To start the mock server for Jio.com API recordings, use the following command:

    ```shell
    
    java -jar ./src/test/resources/specmatic/specmatic.jar stub  ./src/test/resources/specmatic/specs/jio_com_api_spec.yaml 
    ```

## Running the tests

You can run the test directly from any IDE, OR, you can run the test from the command line using the command:

> ./gradlew clean test --tests [JioRecharge_UFG_Test.java](src/test/java/com/eot/e2e/negative/JioRecharge_UFG_Test.java)