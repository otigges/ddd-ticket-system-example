package ticket.infrastructure.rest;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ticket.application.TicketService;
import ticket.domain.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@RestController
public class TicketRestEndpoint {

    private LinkBuilder linkBuilder;

    private TicketService ticketService;

    @Autowired
    public TicketRestEndpoint(TicketService ticketService, LinkBuilder linkBuilder) {
        this.ticketService = ticketService;
        this.linkBuilder = linkBuilder;
    }

    // -- API

    @GetMapping("/")
    public ResponseEntity<?>  getServiceDocument(HttpServletRequest request) {
        ServiceInfo info = new ServiceInfo(request);

        Document<ServiceInfo> document = new Document<>(info);
        document.addLink(new Link(linkBuilder.linkToTicketSearch(), "tickets").addMethods("GET", "POST"));
        document.addLink(new Link(linkBuilder.linkToInfoEndpoint(), "info"));
        document.addLink(new Link(linkBuilder.linkToHealthEndpoint(), "health"));
        return new ResponseEntity<>(document, HttpStatus.OK);
    }

    @GetMapping("/tickets")
    public ResponseEntity<?> getTickets() {
        List<Ticket> tickets = ticketService.searchTicket(SearchCriteria.any());
        TicketsSearchResultTO result = new TicketsSearchResultTO(tickets, linkBuilder);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/tickets/{id}")
    public ResponseEntity<?>  getTicket(@PathVariable(value="id") int id) {
        return ticketService.findTicket(new TicketID(id))
             .map(ticket -> {
                 Document<TicketTO> doc = new Document<>(TicketTO.from(ticket));
                 doc.addLink(Link.self(linkBuilder.linkTo(ticket)).addMethods("GET", "PUT", "DELETE"));

                 Set<Action> allowedActions = ticket.getAllowedActions();
                 for (Action action : allowedActions) {
                     doc.addLink(new Link(linkBuilder.linkToAction(ticket, action), action.toString().toLowerCase())
                             .addMethods("POST"));
                 }

                 doc.addLink(new Link(linkBuilder.linkToWatchers(ticket), "watchers").addMethods("GET", "POST"));
                 doc.addLink(new Link(linkBuilder.linkToAssign(ticket), "assign").addMethods("POST"));
                 doc.addLink(new Link(linkBuilder.linkToAttachments(ticket), "attachments").addMethods("GET", "POST"));
                 doc.addLink(new Link(linkBuilder.linkToComments(ticket), "comments").addMethods("GET", "POST"));
                 return new ResponseEntity<>(doc, HttpStatus.OK);
            })
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

    @PostMapping("/tickets/{id}/{action}")
    public ResponseEntity<?> perform(@PathVariable(value="id") int id, @PathVariable(value="action") String actionName) {
        return ticketService.findTicket(new TicketID(id))
                .map(ticket -> {
                    try {
                        ticket.apply(Action.from(actionName));
                        ticketService.update(ticket);
                        return new ResponseEntity<>(HttpStatus.OK);
                    } catch (IllegalStateTransitionException e) {
                        return new ResponseEntity<>("Action " + actionName
                                + " is not allowed. Allowed actions are: " + ticket.getAllowedActions(),
                                HttpStatus.CONFLICT);
                    }
                })
                .orElse(notFound());
    }

    @GetMapping("/tickets/{id}/watchers")
    public ResponseEntity<?>  getTicketWatchers(@PathVariable(value="id") int id) {
        return ticketService.findTicket(new TicketID(id))
                .map(ticket -> {
                    Document<WatcherListTO> doc = new Document<>(WatcherListTO.from(ticket, linkBuilder));
                    doc.addLink(new Link(linkBuilder.linkToWatchers(ticket), "add_watchers"));
                    return new ResponseEntity<>(doc, HttpStatus.OK);
                })
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/tickets/{ticketId}/watchers/{watcherId}")
    public ResponseEntity<?>  getTicketWatcher(
            @PathVariable(value="ticketId") int ticketId,
            @PathVariable(value="watcherId") String watcherName) {
        Set<UserID> watchers = ticketService.findTicket(new TicketID(ticketId))
                .map(Ticket::getWatchers).orElse(Collections.emptySet());
        UserID watcherId = new UserID(watcherName);
        if (watchers.contains(watcherId)) {
            Link selfLink = Link.self(linkBuilder.linkToWatcher(new TicketID(ticketId), watcherId))
                    .addMethods("GET", "PUT", "DELETE");
            Document<WatcherTO> doc = new Document<>(new WatcherTO(watcherName, selfLink));
            return new ResponseEntity<>(doc, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/tickets/{ticketId}/watchers/{watcherId}")
    public ResponseEntity<?> putTicketWatcher(
            @PathVariable(value="ticketId") int ticketId,
            @PathVariable(value="watcherId") String watcherId) {
        return ticketService.findTicket(new TicketID(ticketId))
                .map(t -> {
                    t.watch(new UserID(watcherId));
                    ticketService.update(t);
                    return ok();
                })
                .orElse(notFound());
    }



    @DeleteMapping("/tickets/{ticketId}/watchers/{watcherId}")
    public ResponseEntity<?>  removeTicketWatcher(
            @PathVariable(value="ticketId") int ticketId,
            @PathVariable(value="watcherId") String watcherId) {
        return ticketService.findTicket(new TicketID(ticketId))
                .map(t -> {
                    t.unwatch(new UserID(watcherId));
                    ticketService.update(t);
                    return ok();
                })
                .orElse(notFound());
    }

    // -- internal

    private ResponseEntity<?> responseTicketCreated(Ticket ticket) {
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(linkBuilder.linkTo(ticket));
        return new ResponseEntity<Void>(headers, HttpStatus.CREATED);
    }

    @NotNull
    private ResponseEntity<Void> ok() {
        return new ResponseEntity<>(HttpStatus.OK);
    }

    private ResponseEntity<Void> notFound() {
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

}