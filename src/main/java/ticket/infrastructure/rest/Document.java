package ticket.infrastructure.rest;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

import java.util.ArrayList;
import java.util.List;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class Document<T> {

    @JsonUnwrapped
    private final T entity;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private final List<Link> links = new ArrayList<>();

    public Document(T entity) {
        this.entity = entity;
    }

    protected Document() {
        this(null);
    }

    public void addLink(Link link) {
        this.links.add(link);
    }

}
