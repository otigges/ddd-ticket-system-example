package ticket.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class StateMachine {

    private final List<Transition> transitions = new ArrayList<>();
    {
        transitions.add(new Transition(Status.NEW, Action.START_PROGRESS, Status.IN_PROGRESS));
        transitions.add(new Transition(Status.IN_PROGRESS, Action.RESOLVE, Status.RESOLVED));
        transitions.add(new Transition(Status.RESOLVED, Action.CLOSE, Status.CLOSED));
        transitions.add(new Transition(Status.CLOSED, Action.ARCHIVE, Status.ARCHIVED));
        transitions.add(new Transition(Status.RESOLVED, Action.REOPEN, Status.OPEN));
        transitions.add(new Transition(Status.CLOSED, Action.REOPEN, Status.OPEN));
        transitions.add(new Transition(Status.ARCHIVED, Action.REOPEN, Status.OPEN));
    }

    private Status currentState;

    public StateMachine(Status currentState) {
        this.currentState = currentState;
    }

    public StateMachine() {
        this(Status.NEW);
    }

    public Status apply(Action action) throws IllegalStateTransitionException {
        for (Transition transition: transitions) {
            if (transition.from == currentState && transition.action == action) {
                currentState = transition.to;
                return currentState;
            }
        }
        throw new IllegalStateTransitionException("Cannot apply action " + action + " when in state " + currentState);
    }

    public Status getCurrentState() {
        return currentState;
    }

    public Set<Action> getAllowedActions() {
        return transitions.stream()
                .filter( (t) -> t.from == currentState )
                .map( (t) -> t.action )
                .collect(Collectors.toSet());
    }

    public String toString() {
        return "State[" + currentState + "]";
    }

    private class Transition {

        final Status from;
        final Action action;
        final Status to;

        public Transition(Status from, Action action, Status to) {
            this.from = from;
            this.action = action;
            this.to = to;
        }

    }

}
