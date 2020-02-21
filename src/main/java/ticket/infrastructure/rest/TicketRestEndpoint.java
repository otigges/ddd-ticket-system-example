package ticket.infrastructure.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import ticket.application.TicketService;
import ticket.domain.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@RestController
public class TicketRestEndpoint {

    public static final String BASE_URI = "http://localhost:8080/";

    public static final String ACTUATOR_URI = BASE_URI + "actuator/";

    private TicketService ticketService;

    @Autowired
    public TicketRestEndpoint(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @GetMapping("/")
    public ResponseEntity<?>  getServiceDocument(HttpServletRequest request) {
        ServiceInfo info = new ServiceInfo(request);

        Document<ServiceInfo> document = new Document<>(info);
        document.addLink(new Link(BASE_URI + "tickets", "tickets").addMethods("GET", "POST"));
        document.addLink(new Link(ACTUATOR_URI + "info", "info"));
        document.addLink(new Link(ACTUATOR_URI + "health", "health"));
        return new ResponseEntity<>(document, HttpStatus.OK);
    }

    @GetMapping("/tickets")
    public ResponseEntity<?>  getTickets() {
        List<Ticket> tickets = ticketService.searchTicket(SearchCriteria.any());
        return new ResponseEntity<>(new TicketsSearchResultTO(tickets), HttpStatus.OK);
    }

    @GetMapping("/tickets/{id}")
    public ResponseEntity<?>  getTicket(@PathVariable(value="id") int id) {
        Optional<Ticket> ticket = ticketService.findTicket(new TicketID(id));
        return ResponseEntity.of(ticket.map(this::toDoc));
    }

    @PostMapping("/tickets")
    public ResponseEntity<?> createTicket(@Valid @RequestBody TicketCreateRequestTO tcr, UriComponentsBuilder b) {
        UserID reporter = new UserID(tcr.getReporter());
        Ticket ticket = ticketService.createTicket(reporter, tcr.getTitle(), tcr.getDescription());
        ticket.watch(reporter);
        ticketService.update(ticket);
        return responseTicketCreated(b, ticket);
    }

    @PostMapping("/tickets/sample")
    public ResponseEntity<?> createSampleTicket(UriComponentsBuilder b) {
        UserID reporter = new UserID("Jane");
        Ticket ticket = ticketService.createTicket(reporter, "This is a sample ticket.", "Some details.");
        ticket.watch(reporter);
        ticketService.update(ticket);
        return responseTicketCreated(b, ticket);
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
    public ResponseEntity<?>  getTicketWatchers(@PathVariable(value="id") int id, UriComponentsBuilder b) {
        Optional<Ticket> ticket = ticketService.findTicket(new TicketID(id));
        return ResponseEntity.of(ticket.map(this::toWatchersDoc));
    }

    @GetMapping("/tickets/{ticketId}/watchers/{watcherId}")
    public ResponseEntity<?>  getTicketWatcher(
            @PathVariable(value="ticketId") int ticketId,
            @PathVariable(value="watcherId") String watcherId,
            UriComponentsBuilder b) {
        Set<UserID> watchers = ticketService.findTicket(new TicketID(ticketId))
                .map(Ticket::getWatchers).orElse(Collections.emptySet());
        if (watchers.contains(new UserID(watcherId))) {
            URI selfLink = b.path("/tickets/{ticketId}/watchers/{watcherId}")
                    .buildAndExpand(ticketId, watcherId)
                    .toUri();

            Document<WatcherTO> doc = new Document<>(
                    new WatcherTO(watcherId, Link.self(selfLink.toString()).addMethods("GET", "PUT", "DELETE")));
            return new ResponseEntity<>(doc, HttpStatus.OK);
        } else {
            return notFound();
        }
    }

    @PutMapping("/tickets/{ticketId}/watchers/{watcherId}")
    public ResponseEntity<?> putTicketWatcher(
            @PathVariable(value="ticketId") int ticketId,
            @PathVariable(value="watcherId") String watcherId,
            UriComponentsBuilder b) {
        return ticketService.findTicket(new TicketID(ticketId))
                .map(t -> {
                    t.watch(new UserID(watcherId));
                    ticketService.update(t);
                    return new ResponseEntity<Void>(HttpStatus.OK);
                })
                .orElse(notFound());
    }

    @DeleteMapping("/tickets/{ticketId}/watchers/{watcherId}")
    public ResponseEntity<?>  removeTicketWatcher(
            @PathVariable(value="ticketId") int ticketId,
            @PathVariable(value="watcherId") String watcherId,
            UriComponentsBuilder b) {
        return ticketService.findTicket(new TicketID(ticketId))
                .map(t -> {
                    t.unwatch(new UserID(watcherId));
                    ticketService.update(t);
                    return new ResponseEntity<Void>(HttpStatus.OK);
                })
                .orElse(notFound());
    }

    // -- internal

    private ResponseEntity<?> responseTicketCreated(UriComponentsBuilder b, Ticket ticket) {
        UriComponents uriComponents =
                b.path("/tickets/{id}").buildAndExpand(ticket.getId());

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(uriComponents.toUri());
        return new ResponseEntity<Void>(headers, HttpStatus.CREATED);
    }

    private Document<?> toDoc(Ticket ticket) {
        Document<TicketTO> doc = new Document<>(TicketTO.from(ticket));
        String basePath = BASE_URI + "tickets/" + ticket.getId();

        doc.addLink(Link.self(basePath).addMethods("GET", "PUT", "DELETE"));
        for (Action action : ticket.getAllowedActions()) {
            String actionName = action.toString().toLowerCase();
            doc.addLink(new Link(basePath + "/" + actionName, actionName).addMethods("POST"));
        }
        doc.addLink(new Link(basePath + "/watchers", "watchers").addMethods("GET", "POST"));
        doc.addLink(new Link(basePath + "/assign", "assign").addMethods("POST"));
        doc.addLink(new Link(basePath + "/attachments", "attach_file").addMethods("GET", "POST"));
        doc.addLink(new Link(basePath + "/comments", "add_comment").addMethods("GET", "POST"));
        return doc;
    }

    private Document<WatcherListTO> toWatchersDoc(Ticket ticket) {
        String basePath = BASE_URI + "tickets/" + ticket.getId() + "/";
        WatcherListTO list = new WatcherListTO();
        Document<WatcherListTO> doc = new Document<>(list);
        doc.addLink(new Link(basePath + "watchers", "add_watchers"));
        for (UserID watcher : ticket.getWatchers()) {
            String name = watcher.toString();
            Link link = Link.self(basePath + "watchers/" + name);
            link.addMethods("GET", "PUT", "DELETE");
            WatcherTO watcherTO = new WatcherTO(name, link);
            list.addWatcher(watcherTO);
        }
        return doc;
    }

    private ResponseEntity<Void> notFound() {
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

}