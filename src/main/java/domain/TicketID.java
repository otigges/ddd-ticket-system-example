package domain;

import stereotypes.ValueObject;

@ValueObject
public class TicketID {

    private final int id;

    public TicketID(int id) {
        this.id = id;
    }

        @Override
    public String toString() {
        return "" + id;
    }
}
