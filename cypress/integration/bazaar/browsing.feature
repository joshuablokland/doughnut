Feature: Bazaar browsing
  Part of the bazaar should be visible to everyone.

  Background:
    Given there are some notes for existing user "old_learner"
      | title           | description                      | testingParent | hideTitleInArticle | showAsBulletInArticle |
      | Shape           | The form of something            |               | false              | false                 |
      | Rectangle       | four equal straight sides        | Shape         | false              | false                 |
      | Triangle        | three sides shape                | Shape         | false              | false                 |
      | Square          | a square but big                 | Rectangle     | false              | false                 |
      | In OOP          | a square is not a Rectangle      | Rectangle     | true               | false                 |
      | interface       | their interfaces are different   | In OOP        | true               | true                  |
      | precondition    | square has stronger precondition | In OOP        | true               | true                  |
      | Shapes are good |                                  | Shape         | false              | false                 |
    And there is "a specialization of" link between note "Square" and "Rectangle"
    And notebook "Shape" is shared to the Bazaar

  Scenario: Browsing as non-user
    When I haven't login
    Then I should see "Shape" is shared in the Bazaar
    And there shouldn't be any note edit button for "Shape"
    When I open the notebook "Shape" in the Bazaar
    Then there shouldn't be any note edit button for "Rectangle"
    And I should see "Bazaar, Shape" in breadcrumb
    And I should be able to go to the "next" note "Rectangle"
    And I should see it has link to "Square"

  Scenario: Browsing as non-user in article view
    When I haven't login
    Then I should see "Shape" is shared in the Bazaar
    When I open the notebook "Shape" in the Bazaar in article view
    Then I should see in the article:
      | level | title    |
      | h1    | Shape    |
      | h2    | Triangle |
      | h3    | Square   |
    And  I should not see "In OOP" in the article
    And  I should see two bullets in the article
    And  I should see "Shapes are good" as non-title in the article

#  Scenario: Breadcrumb should be until the share point
