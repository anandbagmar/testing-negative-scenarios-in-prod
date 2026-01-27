@jio @specmatic @invalid
Feature: Non-Jio numbers should not be able to recharge

#  CONFIG=./teswiz_configs/configs/jio_local_web_config.properties TAG="@invalidJioNumber" ./gradlew run
#  CONFIG=./teswiz_configs/configs/jio_local_android_config.properties TAG="@invalidJioNumber" ./gradlew run
  @android @web @invalidJioNumber
  Scenario: User should be able to do a recharge for a postpaid number for a valid amount
    Given I have a non-Jio number
    When I recharge the non-Jio number
    Then I should see an invalid Jio number error message
