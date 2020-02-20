package ticket.domain;

import stereotypes.DomainEvent;

import java.time.LocalDateTime;

@DomainEvent
public class TicketClosed extends TicketEvent {

    public static final String TYPE = "TicketClosed";

    public TicketClosed(TicketID id, String payload, LocalDateTime timestamp) {
        super(id, TYPE, payload, timestamp);
    }

    public TicketClosed(TicketID id, String payload) {
        super(id, TYPE, payload);
    }
}
