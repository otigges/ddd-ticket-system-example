Feature: Creation of new tickets.

  Scenario: Ticket creators are added to watch list
    Given No tickets exist
    When Jane creates new ticket
    Then Jane is in the list of watchers

  Scenario: New tickets are in status NEW
    Given No tickets exist
    When Jane creates new ticket
    Then ticket is in status NEW
    And START_PROGRESS is an allowed action
    And REOPEN is not an allowed action

  Scenario Outline: Allowed actions
    Given a ticket in status <status>
    Then <valid> is an allowed action
    And <invalid> is not an allowed action

    Examples:
      | status          | valid             | invalid             |
      |    NEW          |  START_PROGRESS   | CLOSE               |
      |    IN_PROGRESS  |  RESOLVE          | START_PROGRESS      |
      |    RESOLVED     |  REOPEN           | START_PROGRESS      |
      |    CLOSED       |  REOPEN           | CLOSE               |