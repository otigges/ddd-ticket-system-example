package ticket.domain;

/**
 * Actions that can be performed on a ticket.
 */
public enum Action {
    START_PROGRESS,
    RESOLVE,
    CLOSE,
    ARCHIVE,
    REOPEN;

    public static Action from(String name) {
        return Action.valueOf(name.toUpperCase());
    }
}
