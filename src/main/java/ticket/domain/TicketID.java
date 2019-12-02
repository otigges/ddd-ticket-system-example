package ticket.domain;

import stereotypes.ValueObject;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TicketID ticketID = (TicketID) o;
        return id == ticketID.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
