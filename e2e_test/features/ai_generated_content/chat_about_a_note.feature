@usingMockedOpenAiService
Feature: Chat about a note with AI
  Learner wants to chat with the AI about a certain note,
  so that they can understand the note better.


  Background:
    Given I am logged in as an existing user
    And I have a note with the topic "There are 42 prefectures in Japan"


  Scenario: The users can chat with AI about the current note
    Given OpenAI assistant will reply below for user messages:
      | user message          | assistant reply              | run id |
      | Is Naba one of them?  | No. It is not.               | run1   |
      | Is this note correct? | No, there are 47 prefectures | run2   |
    When I start to chat about the note "There are 42 prefectures in Japan"
    And I send the message "Is Naba one of them?" to AI
    Then I should receive the following chat messages:
      | role      | message              |
      | user      | Is Naba one of them? |
      | assistant | No. It is not.       |
    When I send the message "Is this note correct?" to AI
    Then I should receive the following chat messages:
      | role      | message                      |
      | user      | Is Naba one of them?         |
      | assistant | No. It is not.               |
      | user      | Is this note correct?        |
      | assistant | No, there are 47 prefectures |

  @ignore
  Scenario: The users can resume chat with AI about the current note
    Given OpenAI assistant will reply below for user messages:
      | user message          | assistant reply              | run id |
      | Is Naba one of them?  | No. It is not.               | run1   |
    When I start to chat about the note "There are 42 prefectures in Japan"
    And I send the message "Is Naba one of them?" to AI
    Then I should receive the following chat messages:
      | role      | message              |
      | user      | Is Naba one of them? |
      | assistant | No. It is not.       |
    When I start to chat about the note "There are 42 prefectures in Japan"
    Then I should receive the following chat messages:
      | role      | message              |
      | user      | Is Naba one of them? |
      | assistant | No. It is not.       |
