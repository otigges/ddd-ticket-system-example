package ticket.infrastructure.rest;

public class Link {

    private final String target;

    private final String rel;

    public Link(String target, String rel) {
        this.target = target;
        this.rel = rel;
    }

    public String getTarget() {
        return target;
    }

    public String getRel() {
        return rel;
    }

}


