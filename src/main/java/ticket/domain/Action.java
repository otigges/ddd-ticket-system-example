package ticket.domain;

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
