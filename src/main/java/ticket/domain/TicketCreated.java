package ticket.domain;

import stereotypes.DomainEvent;

import java.time.LocalDateTime;

@DomainEvent
public class TicketCreated extends TicketEvent {

    public static final String TYPE = "TicketCreated";

    public TicketCreated(String payload, LocalDateTime timestamp) {
        super(TYPE, payload, timestamp);
    }

    public TicketCreated(String payload) {
        super(TYPE, payload);
    }
}
