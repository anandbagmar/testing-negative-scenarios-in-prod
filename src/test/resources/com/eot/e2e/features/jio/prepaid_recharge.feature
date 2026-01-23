@jio @specmatic @prepaid
Feature: Prepaid number recharge scenarios

#  CONFIG=./teswiz_configs/configs/jio_local_web_config.properties TAG="@0Plans" ./gradlew run
  @web @0Plans
  Scenario: User should see 0 plans available message for prepaid number having zero plans
    Given I have a prepaid number "PREPAID_PHONE_NUMBER_0_PLANS" with zero plans
    When I recharge the prepaid number
    Then I should see "ZERO_PLANS_ERROR_MESSAGE" error message

##  CONFIG=./teswiz_configs/configs/jio_local_web_config.properties TAG="@1Plans" ./gradlew run
#  @web @1Plan
#  Scenario: User should see 1 plan available message for prepaid number having 1 plan
#    Given I have a prepaid number "PREPAID_PHONE_NUMBER_1_PLAN" with zero plans
#    When I recharge the prepaid number
#    Then I should see "1" plan available message
#
##  CONFIG=./teswiz_configs/configs/jio_local_web_config.properties TAG="@2Plans" ./gradlew run
#  @web @2Plans
#  Scenario: User should see 2 plans available message for prepaid number having 2 plans
#    Given I have a prepaid number "PREPAID_PHONE_NUMBER_2_PLANS" with zero plans
#    When I recharge the prepaid number
#    Then I should see "2" plans available message
#
##  CONFIG=./teswiz_configs/configs/jio_local_web_config.properties TAG="@5Plans" ./gradlew run
#  @web @5Plans
#  Scenario: User should see 5 plans available message for prepaid number having 5 plans
#    Given I have a prepaid number "PREPAID_PHONE_NUMBER_5_PLANS" with zero plans
#    When I recharge the prepaid number
#    Then I should see "5" plans available message
#
##  CONFIG=./teswiz_configs/configs/jio_local_web_config.properties TAG="@10Plans" ./gradlew run
#  @web @10Plans
#  Scenario: User should see 10 plans available message for prepaid number having 10 plans
#    Given I have a prepaid number "PREPAID_PHONE_NUMBER_10_PLANS" with zero plans
#    When I recharge the prepaid number
#    Then I should see "10" plans available message

  #  CONFIG=./teswiz_configs/configs/jio_local_web_config.properties TAG="@PrepaidPlans" ./gradlew run
  @web @PrepaidPlans
  Scenario Outline: User should see <NumberOfPlansMessageExpected> plans available message for prepaid number having <NumberOfPlansMessageExpected> plans
    Given I have a prepaid number "<PrepaidNumberVariable>" with zero plans
    When I recharge the prepaid number
    Then I should see <NumberOfPlansMessageExpected> plans available message
    Examples:
      | PrepaidNumberVariable         | NumberOfPlansMessageExpected |
      | PREPAID_PHONE_NUMBER_10_PLANS | "10"                         |
      | PREPAID_PHONE_NUMBER_5_PLANS  | "5"                          |
      | PREPAID_PHONE_NUMBER_2_PLANS  | "2"                          |
      | PREPAID_PHONE_NUMBER_1_PLAN   | "1"                          |
