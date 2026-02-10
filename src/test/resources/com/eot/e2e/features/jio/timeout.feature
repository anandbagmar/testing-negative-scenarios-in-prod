@jio @specmatic @invalid
Feature: Verify timeout scenarios for dependent services

#  CONFIG=./teswiz_configs/configs/jio_local_web_config.properties TAG="@0PlanTimeout" ./gradlew run
#  CONFIG=./teswiz_configs/configs/jio_local_android_config.properties TAG="@0PlanTimeout" ./gradlew run
  @android @web @0PlanTimeout
  Scenario: When the service times out for a prepaid number with 0 plans, the user should see a “Something went wrong” error message, and upon retry, the 0 plans should be displayed
    Given I have a prepaid number "PREPAID_PHONE_NUMBER_0PLANS_TIMEOUT" with "0" plans
    When I try to recharge the prepaid number
    Then I should see "SOMETHING_WENT_WRONG_ERROR_MESSAGE" error message when the service times out
    When I retry recharging the prepaid number
    Then I should see "ZERO_PLANS_ERROR_MESSAGE" error message


#  CONFIG=./teswiz_configs/configs/jio_local_web_config.properties TAG="@1PlanTimeout" ./gradlew run
#  CONFIG=./teswiz_configs/configs/jio_local_android_config.properties TAG="@1PlanTimeout" ./gradlew run
  @android @web @1PlanTimeout
  Scenario: When the service times out for a prepaid number with 1 plan, the user should see a “Something went wrong” error message, and upon retry, the single plan should be displayed
    Given I have a prepaid number "PREPAID_PHONE_NUMBER_1PLAN_TIMEOUT" with "1" plans
    When I try to recharge the prepaid number
    Then I should see "SOMETHING_WENT_WRONG_ERROR_MESSAGE" error message when the service times out
    When I retry recharging the prepaid number
    Then I should see "1" plans available message