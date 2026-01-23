@jio @specmatic @invalid
Feature: Non-Jio numbers should not be able to recharge

#  CONFIG=./src/test/resources/configs/jio_local_web_config.properties TAG="@invalidJioNumber" PLATFORM=web ./gradlew run
  @web @invalidJioNumber
  Scenario: User should be able to do a recharge for a postpaid number for a valid amount
    Given I have a non-Jio number
    When I recharge the non-Jio number
    Then I should see an invalid Jio number error message
