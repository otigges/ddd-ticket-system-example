package ticket.infrastructure.rest;

public class WatcherTO {

    private final String name;
    private final Link link;

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

