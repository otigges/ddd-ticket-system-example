package ticket.infrastructure.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import ticket.application.TicketService;
import ticket.domain.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
public class TicketRestEndpoint {

    public static final UserID JANE = new UserID("Jane");

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

    @GetMapping("/tickets/{id}/watchers")
    public ResponseEntity<?>  getTicketWatchers(@PathVariable(value="id") int id) {
        Optional<Ticket> ticket = ticketService.findTicket(new TicketID(id));
        return ResponseEntity.of(ticket.map(this::toWatchersDoc));
    }

    @PostMapping(
        value = "/tickets",
        consumes = { MediaType.APPLICATION_JSON_VALUE }
    )
    public ResponseEntity<?> createTicket(@Valid @RequestBody TicketCreateRequestTO tcr, UriComponentsBuilder b) {
        UserID reporter = new UserID(tcr.getReporter());
        Ticket ticket = ticketService.createTicket(reporter, tcr.getTitle(), tcr.getDescription());
        ticket.watch(reporter);
        ticketService.update(ticket);
        return responseTicketCreated(b, ticket);
    }

    @PostMapping("/tickets/sample")
    public ResponseEntity<?> createSampleTicket(UriComponentsBuilder b) {
        Ticket ticket = ticketService.createTicket(JANE, "This is a sample ticket.", "Some details.");
        ticket.watch(JANE);
        ticketService.update(ticket);
        return responseTicketCreated(b, ticket);
    }

    @PostMapping("/tickets/{id}/{action}")
    public ResponseEntity<?> perform(@PathVariable(value="id") int id, @PathVariable(value="action") String actionName) {
        Optional<Ticket> optional = ticketService.findTicket(new TicketID(id));
        if (optional.isPresent()) {
            Ticket ticket = optional.get();
            Action action = Action.valueOf(actionName.toUpperCase());
            try {
                ticket.apply(action);
                ticketService.update(ticket);
                return new ResponseEntity<>(HttpStatus.OK);
            } catch (IllegalStateTransitionException e) {
                return new ResponseEntity<>("Action " + action
                        + " is not allowed. Allowed actions are: " + ticket.getAllowedActions(),
                        HttpStatus.CONFLICT);
            }
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
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
            Link link = new Link(basePath + "watchers/" + name, "self");
            link.addMethods("GET", "PUT", "DELETE");
            WatcherTO watcherTO = new WatcherTO(name, link);
            list.addWatcher(watcherTO);
        }
        return doc;
    }





}