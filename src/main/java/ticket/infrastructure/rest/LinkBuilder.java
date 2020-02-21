package ticket.infrastructure.rest;

import org.springframework.web.util.UriComponentsBuilder;
import ticket.domain.Action;
import ticket.domain.Ticket;
import ticket.domain.TicketID;
import ticket.domain.UserID;

import java.net.URI;

public class LinkBuilder {

    private URI baseUri;

    public LinkBuilder(URI baseUri) {
        this.baseUri = baseUri;
    }

    // links

    public URI linkToTicketSearch() {
        return ucb().path("/tickets").build().toUri();
    }

    public URI linkTo(Ticket ticket) {
        return ucb().path("/tickets/{id}").buildAndExpand(ticket.getId()).toUri();
    }

    public URI linkToWatchers(Ticket ticket) {
        return ucb().path("/tickets/{id}/watchers").buildAndExpand(ticket.getId()).toUri();
    }

    public URI linkToAttachments(Ticket ticket) {
        return ucb().path("/tickets/{id}/attachments").buildAndExpand(ticket.getId()).toUri();
    }

    public URI linkToComments(Ticket ticket) {
        return ucb().path("/tickets/{id}/comments").buildAndExpand(ticket.getId()).toUri();
    }

    public URI linkToAssign(Ticket ticket) {
        return ucb().path("/tickets/{id}/assign").buildAndExpand(ticket.getId()).toUri();
    }

    public URI linkToWatcher(Ticket ticket, UserID watcher) {
        return linkToWatcher(ticket.getId(), watcher);
    }

    public URI linkToWatcher(TicketID ticketId, UserID watcher) {
        return ucb().path("/tickets/{ticketId}/watchers/{watcherId}")
                .buildAndExpand(ticketId, watcher).toUri();
    }

    public URI linkToAction(Ticket ticket, Action action) {
        return ucb().path("/tickets/{ticketId}/{action}")
                .buildAndExpand(ticket.getId(), action.toString().toLowerCase()).toUri();
    }

    public URI linkToInfoEndpoint() {
        return ucb().path("/actuator/info").build().toUri();
    }

    public URI linkToHealthEndpoint() {
        return ucb().path("/actuator/health").build().toUri();
    }



    // internal

    private UriComponentsBuilder ucb() {
        return UriComponentsBuilder.fromUri(baseUri);
    }

}
