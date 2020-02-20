package ticket.domain;

import stereotypes.DomainEvent;

import java.time.LocalDateTime;

@DomainEvent
public class TicketCreated extends TicketEvent {

    public static final String TYPE = "TicketCreated";

    public TicketCreated(TicketID id, String payload, LocalDateTime timestamp) {
        super(id, TYPE, payload, timestamp);
    }

    public TicketCreated(TicketID id, String payload) {
        super(id, TYPE, payload);
    }
}
