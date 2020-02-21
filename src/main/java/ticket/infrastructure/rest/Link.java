package ticket.infrastructure.rest;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.net.URI;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Link {

    private final String target;

    private final String rel;

    private final Set<String> methods = new HashSet<>();

    public static Link self(String target) {
        return new Link(target, "self");
    }

    public static Link self(URI target) {
        return self(target.toString());
    }

    public Link(String target, String rel) {
        this.target = target;
        this.rel = rel;
    }

    public Link(URI target, String rel) {
        this(target.toString(), rel);
    }

    public Link addMethods(String... methods) {
        this.methods.addAll(Arrays.asList(methods));
        return this;
    }

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public Set<String> getMethods() {
        return methods;
    }

    public String getTarget() {
        return target;
    }

    public String getRel() {
        return rel;
    }

}


