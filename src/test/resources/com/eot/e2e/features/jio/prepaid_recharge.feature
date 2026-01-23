@jio @specmatic
Feature: Prepaid number recharge scenarios

#  CONFIG=./src/test/resources/configs/jio_local_web_config.properties TAG="@0Plans" PLATFORM=web ./gradlew run
  @web @0Plans
  Scenario: User should see 0 plans available message for prepaid number having zero plans
    Given I have a prepaid number "PREPAID_PHONE_NUMBER_0_PLANS" with zero plans
    When I recharge the prepaid number
    Then I should see "ZERO_PLANS_ERROR_MESSAGE" error message