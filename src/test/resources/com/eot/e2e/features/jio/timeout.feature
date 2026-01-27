@jio @specmatic @invalid
Feature: Verify timeout scenarios for dependent services

#  CONFIG=./teswiz_configs/configs/jio_local_web_config.properties TAG="@timeout" ./gradlew run
#  CONFIG=./teswiz_configs/configs/jio_local_web_config.properties TAG="@timeout" ./gradlew run
  @android @web @timeout @0Plans
  Scenario: User should see something went wrong error message when the service times out for prepaid number having zero plans
    Given I have a prepaid number "PREPAID_PHONE_NUMBER_TIMEOUT" with zero plans
    When I try to recharge the prepaid number
    Then I should see "SOMETHING_WENT_WRONG_ERROR_MESSAGE" error message when the service times out
