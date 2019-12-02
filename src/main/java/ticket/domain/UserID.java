package ticket.domain;

import java.util.UUID;

public class UserID {

    private final String id;

    public UserID(String id) {
        this.id = id;
    }

    public UserID() {
        this(UUID.randomUUID().toString());
    }

    @Override
    public String toString() {
        return id;
    }
}
