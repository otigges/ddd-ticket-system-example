package ticket.infrastructure.rest;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Link {

    private final String target;

    private final String rel;

    private final Set<String> methods = new HashSet<>();

    public Link(String target, String rel) {
        this.target = target;
        this.rel = rel;
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


