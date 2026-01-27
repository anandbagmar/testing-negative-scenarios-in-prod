@jio @specmatic @prepaid
Feature: Prepaid number recharge scenarios

#  CONFIG=./teswiz_configs/configs/jio_local_web_config.properties TAG="@0Plans" ./gradlew run
#  CONFIG=./teswiz_configs/configs/jio_local_android_config.properties TAG="@0Plans" ./gradlew run
  @android @web @0Plans
  Scenario: User should see 0 plans available message for prepaid number having zero plans
    Given I have a prepaid number "PREPAID_PHONE_NUMBER_0_PLANS" with "0" plans
    When I recharge the prepaid number
    Then I should see "ZERO_PLANS_ERROR_MESSAGE" error message

  #  CONFIG=./teswiz_configs/configs/jio_local_web_config.properties TAG="@PrepaidPlans" ./gradlew run
  #  CONFIG=./teswiz_configs/configs/jio_local_android_config.properties TAG="@PrepaidPlans" ./gradlew run
  @android @web @PrepaidPlans
  Scenario Outline: User should see <NumberOfPlansMessageExpected> plans available message for prepaid number having <NumberOfPlansMessageExpected> plans
    Given I have a prepaid number "<PrepaidNumberVariable>" with <NumberOfPlansMessageExpected> plans
    When I recharge the prepaid number
    Then I should see <NumberOfPlansMessageExpected> plans available message
    Examples:
      | PrepaidNumberVariable         | NumberOfPlansMessageExpected |
      | PREPAID_PHONE_NUMBER_10_PLANS | "10"                         |
      | PREPAID_PHONE_NUMBER_5_PLANS  | "5"                          |
      | PREPAID_PHONE_NUMBER_2_PLANS  | "2"                          |
      | PREPAID_PHONE_NUMBER_1_PLAN   | "1"                          |
