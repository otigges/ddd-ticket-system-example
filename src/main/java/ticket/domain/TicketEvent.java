package ticket.domain;

import java.time.LocalDateTime;

public abstract class TicketEvent {

    private final String type;

    private final String payload;

    private final LocalDateTime timestamp;

    public TicketEvent(String type, String payload, LocalDateTime timestamp) {
        this.type = type;
        this.payload = payload;
        this.timestamp = timestamp;
    }

    public TicketEvent(String type, String payload) {
        this(type, payload, LocalDateTime.now());
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
                "' @" + timestamp +
                ", payload='" + payload + '\'' +
                ']';
    }
}
