package ticket.domain;

import stereotypes.DomainEvent;

import java.time.LocalDateTime;

@DomainEvent
public class TicketClosed extends TicketEvent {

    public static final String TYPE = "TicketClosed";

    public TicketClosed(String payload, LocalDateTime timestamp) {
        super(TYPE, payload, timestamp);
    }

    public TicketClosed(String payload) {
        super(TYPE, payload);
    }
}
