package ticket.domain;

public class IllegalStateTransitionException extends Exception {

    public IllegalStateTransitionException(String message) {
        super(message);
    }
}
