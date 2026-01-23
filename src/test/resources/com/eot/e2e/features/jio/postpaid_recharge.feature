@jio @specmatic @postpaid
Feature: Postpaid number recharge scenarios

#  CONFIG=./src/test/resources/configs/jio_local_web_config.properties TAG="@validRecharge" PLATFORM=web ./gradlew run
  @web @validRecharge
  Scenario: User should be able to do a recharge for a postpaid number for a valid amount
    Given I have a postpaid number
    Then I recharge with a valid recharge amount

#  CONFIG=./src/test/resources/configs/jio_local_web_config.properties TAG="@invalidRecharge" PLATFORM=web ./gradlew run
  @web @invalidRecharge
  Scenario: User should be not be able to do a recharge for a postpaid number with an invalid recharge amount
    Given I have a postpaid number
    When I recharge with an invalid recharge amount
    Then I should see "POSTPAID_MAXIMUM_AMOUNT_ERROR_MESSAGE" error message for invalid recharge amount
