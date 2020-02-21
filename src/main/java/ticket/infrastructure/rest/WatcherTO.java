package ticket.infrastructure.rest;

import ticket.domain.UserID;

public class WatcherTO {

    private final String name;
    private final Link link;

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

