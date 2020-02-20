package ticket.domain;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

public class UserID implements Serializable {

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserID userID = (UserID) o;
        return Objects.equals(id, userID.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}
