package ticket.domain;

import stereotypes.DomainEvent;

import java.time.LocalDateTime;

@DomainEvent
public class TicketChanged extends TicketEvent {

    public static final String TYPE = "TicketChanged";

    public TicketChanged(TicketID id, String payload, LocalDateTime timestamp) {
        super(id, TYPE, payload, timestamp);
    }

    public TicketChanged(TicketID id, String payload) {
        super(id, TYPE, payload);
    }
}
