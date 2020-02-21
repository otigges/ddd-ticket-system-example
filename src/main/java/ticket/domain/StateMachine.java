package ticket.domain;

import java.io.Serializable;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class StateMachine implements Serializable {

    private Status currentState;

    public StateMachine(Status currentState) {
        this.currentState = currentState;
    }

    public StateMachine() {
        this(Status.NEW);
    }

    public Status apply(Action action) throws IllegalStateTransitionException {
        Transition transition = Transition.match(currentState, action);
        currentState = transition.to;
        return currentState;
    }

    public Status getCurrentState() {
        return currentState;
    }

    public Set<Action> getAllowedActions() {
        return Transition.validTransitions(currentState);
    }

    public String toString() {
        return "State[" + currentState + "]";
    }

    private static class Transition {

        private static final List<Transition> transitions = init();

        final Status from;
        final Action action;
        final Status to;

        public Transition(Status from, Action action, Status to) {
            this.from = from;
            this.action = action;
            this.to = to;
        }

        public static Set<Action> validTransitions(Status status) {
            return transitions.stream()
                    .filter( (t) -> t.from.equals(status) )
                    .map( (t) -> t.action )
                    .collect(Collectors.toSet());
        }

        public static Transition match(Status status, Action action) throws IllegalStateTransitionException {
            for (Transition transition: transitions) {
                if (transition.from.equals(status) && transition.action.equals(action) ) {
                    return transition;
                }
            }
            throw new IllegalStateTransitionException("Cannot apply action " + action + " when in state " + status);
        }

        private static List<Transition> init() {
            return List.of(
                    new Transition(Status.NEW, Action.START_PROGRESS, Status.IN_PROGRESS),
                    new Transition(Status.IN_PROGRESS, Action.RESOLVE, Status.RESOLVED),
                    new Transition(Status.RESOLVED, Action.CLOSE, Status.CLOSED),
                    new Transition(Status.CLOSED, Action.ARCHIVE, Status.ARCHIVED),
                    new Transition(Status.RESOLVED, Action.REOPEN, Status.OPEN),
                    new Transition(Status.CLOSED, Action.REOPEN, Status.OPEN),
                    new Transition(Status.ARCHIVED, Action.REOPEN, Status.OPEN)
            );
        }

    }

}
