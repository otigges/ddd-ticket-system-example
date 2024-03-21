package ticket.adapters.rest;

import com.fasterxml.jackson.annotation.JsonInclude;
import ticket.domain.UserID;

public class WatcherTO {

    private final String name;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final Link link;

    public WatcherTO(UserID name) {
        this(name.toString(), null);
    }

    public WatcherTO(UserID name, Link link) {
        this(name.toString(), link);
    }

    public WatcherTO(String name,  Link link) {
        this.name = name;
        this.link = link;
    }

    public Link getLink() {
        return link;
    }

    public String getName() {
        return name;
    }
}

