package ticket.domain;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;

public abstract class TicketEvent {

    private final TicketID ticketID;

    private final String type;

    private final String payload;

    private final LocalDateTime timestamp;

    public TicketEvent(TicketID ticketID, String type, String payload, LocalDateTime timestamp) {
        this.ticketID = ticketID;
        this.type = type;
        this.payload = payload;
        this.timestamp = timestamp;
    }

    public TicketEvent(TicketID ticketID, String type, String payload) {
        this(ticketID, type, payload, LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS));
    }

    public TicketID getTicketID() {
        return ticketID;
    }

    public String getType() {
        return type;
    }

    public String getPayload() {
        return payload;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }


    @Override
    public String toString() {
        return "TicketEvent['" + type +
                "' #" + ticketID +
                " @" + timestamp +
                ", payload='" + payload + '\'' +
                ']';
    }
}
