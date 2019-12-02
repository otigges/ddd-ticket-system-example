package ticket.domain;

import stereotypes.DomainEvent;

import java.time.LocalDateTime;

@DomainEvent
public class TicketChanged extends TicketEvent {

    public static final String TYPE = "TicketChanged";

    public TicketChanged(String payload, LocalDateTime timestamp) {
        super(TYPE, payload, timestamp);
    }

    public TicketChanged(String payload) {
        super(TYPE, payload);
    }
}
