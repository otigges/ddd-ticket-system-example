package ticket.infrastructure.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ticket.application.TicketService;
import ticket.domain.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Set;

import static java.util.Collections.emptySet;

@RestController
public class TicketRestEndpoint {

    private TicketService ticketService;

    private LinkBuilder linkBuilder;

    @Autowired
    public TicketRestEndpoint(TicketService ticketService, LinkBuilder linkBuilder) {
        this.ticketService = ticketService;
        this.linkBuilder = linkBuilder;
    }

    // -- API

    @GetMapping("/tickets")
    public ResponseEntity<?> getTickets() {
        List<Ticket> tickets = ticketService.searchTicket(SearchCriteria.any());
        TicketsSearchResultTO result = new TicketsSearchResultTO(tickets);
        return ok(result);
    }

    @GetMapping("/tickets/{id}")
    public ResponseEntity<?>  getTicket(@PathVariable(value="id") TicketID id) {
        return ticketService.findTicket(id)
             .map(ticket -> ok(TicketTO.from(ticket)))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping("/tickets")
    public ResponseEntity<?> createTicket(@Valid @RequestBody TicketCreateRequestTO tcr) {
        UserID reporter = new UserID(tcr.getReporter());
        Ticket ticket = ticketService.createTicket(reporter, tcr.getTitle(), tcr.getDescription(), reporter);
        return responseTicketCreated(ticket);
    }

    @PostMapping("/tickets/sample")
    public ResponseEntity<?> createSampleTicket() {
        UserID reporter = new UserID("Jane");
        Ticket ticket = ticketService.createTicket(reporter, "This is a sample ticket.", "Some details.", reporter);
        return responseTicketCreated(ticket);
    }

    @RequestMapping(
            value = "/tickets/{id}",
            method = RequestMethod.PATCH
    )
    public ResponseEntity<?> updateTicket(@RequestBody TicketUpdateRequestTO tur,
                                          @PathVariable(value="id") TicketID id) {
        return ticketService.findTicket(id)
                .map(ticket -> {
                    if (tur.descriptionChanged()) {
                        ticket.updateDescription(tur.getDescription());
                    }
                    if (tur.titleChanged()) {
                        ticket.updateTitle(tur.getTitle());
                    }
                    if (tur.assigneeChanged()) {
                        ticket.assignTo(new UserID(tur.getAssignee()));
                    }
                    ticketService.update(ticket);
                    return ok(TicketTO.from(ticket));
                })
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping("/tickets/{id}/{action}")
    public ResponseEntity<?> perform(@PathVariable(value="id") TicketID id, @PathVariable(value="action") String actionName) {
        return ticketService.findTicket(id)
                .map(ticket -> {
                    try {
                        ticket.apply(Action.from(actionName));
                        ticketService.update(ticket);
                        return ok();
                    } catch (IllegalStateTransitionException e) {
                        return new ResponseEntity<>("Action " + actionName
                                + " is not allowed. Allowed actions are: " + ticket.getAllowedActions(),
                                HttpStatus.CONFLICT);
                    }
                })
                .orElse(notFound());
    }

    @GetMapping("/tickets/{id}/watchers")
    public ResponseEntity<?>  getTicketWatchers(@PathVariable(value="id") TicketID id) {
        return ticketService.findTicket(id)
                .map(ticket -> {
                    Document<WatcherListTO> doc = new Document<>(WatcherListTO.from(ticket, linkBuilder));
                    doc.addLink(linkBuilder.linkToWatchers(ticket), "add_watchers");
                    return ok(doc);
                })
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/tickets/{ticketId}/watchers/{watcherId}")
    public ResponseEntity<?>  getTicketWatcher(
            @PathVariable(value="ticketId") TicketID ticketId,
            @PathVariable(value="watcherId") UserID watcher) {
        Set<UserID> watchers = ticketService.findTicket(ticketId)
                .map(Ticket::getWatchers).orElse(emptySet());
        if (watchers.contains(watcher)) {
            Link selfLink = Link.self(linkBuilder.linkToWatcher(ticketId, watcher))
                    .addMethods("GET", "PUT", "DELETE");
            WatcherTO watcherTO = new WatcherTO(watcher, selfLink);
            return ok(new Document<>(watcherTO));
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/tickets/{ticketId}/watchers/{watcherId}")
    public ResponseEntity<?> putTicketWatcher(
            @PathVariable(value="ticketId") TicketID ticketId,
            @PathVariable(value="watcherId") UserID watcher) {
        return ticketService.findTicket(ticketId)
                .map(t -> {
                    t.watch(watcher);
                    ticketService.update(t);
                    return ok();
                })
                .orElse(notFound());
    }

    @DeleteMapping("/tickets/{ticketId}/watchers/{watcherId}")
    public ResponseEntity<?>  removeTicketWatcher(
            @PathVariable(value="ticketId") TicketID ticketId,
            @PathVariable(value="watcherId") UserID watcher) {
        return ticketService.findTicket(ticketId)
                .map(t -> {
                    t.unwatch(watcher);
                    ticketService.update(t);
                    return ok();
                })
                .orElse(notFound());
    }

    /* ##########   EXERCISE No. 3 ############
     * Implement Hypermedia! Add some links to your ticket:
     * - to allowed actions
     * - to watcher list
     * - self link to this ticket
     * Please, take a look at classes Document, TicketTO, and LinkBuilder.
     * E.g. you can add a self link to a ticket if you create it this way:
     *
     * <code>
     *  TicketTO.from(ticket, linkBuilder);
     * </code>
     *
     */

    // -- internal

    private ResponseEntity<?> responseTicketCreated(Ticket ticket) {
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(linkBuilder.linkTo(ticket));
        return new ResponseEntity<Void>(headers, HttpStatus.CREATED);
    }

    private <T> ResponseEntity<T> ok(T entity) {
        return new ResponseEntity<>(entity, HttpStatus.OK);
    }

    private ResponseEntity<Void> ok() {
        return new ResponseEntity<>(HttpStatus.OK);
    }

    private ResponseEntity<Void> notFound() {
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

}