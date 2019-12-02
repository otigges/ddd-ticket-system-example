package ticket.infrastructure.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import ticket.application.TicketService;
import ticket.domain.*;

import javax.print.Doc;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

@RestController
public class TicketRestEndpoint {

    private static final String template = "Hello, %s!";
    public static final UserID JANE = new UserID("Jane");
    private final AtomicLong counter = new AtomicLong();

    private TicketService ticketService;

    @Autowired
    public TicketRestEndpoint(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @RequestMapping("/tickets/{id}")
    public ResponseEntity<?>  getTicket(@PathVariable(value="id") int id) {
        Optional<Ticket> ticket = ticketService.findTicket(new TicketID(id));
        return ResponseEntity.of(ticket.map(this::toDoc));
    }

    @RequestMapping("/tickets/{id}/watchers")
    public ResponseEntity<?>  getTicketWatchers(@PathVariable(value="id") int id) {
        Optional<Ticket> ticket = ticketService.findTicket(new TicketID(id));
        return ResponseEntity.of(ticket.map(this::toWatchersDoc));
    }

    @RequestMapping(
            value = "/tickets/{id}/{action}",
            method = RequestMethod.POST)
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

    @RequestMapping(
            value = "/tickets",
            method = RequestMethod.POST
    )
    public ResponseEntity<?> createSampleTicket(UriComponentsBuilder b) {
        Ticket ticket = ticketService.createTicket(JANE, "Somthing is wrong.", "Some details.");
        ticket.watch(JANE);
        UriComponents uriComponents =
                b.path("/tickets/{id}").buildAndExpand(ticket.getId());

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(uriComponents.toUri());
        return new ResponseEntity<Void>(headers, HttpStatus.CREATED);
    }

    private Document toDoc(Ticket ticket) {
        Document<TicketTO> doc = new Document<>(map(ticket));
        String basePath = "http://localhost:8080/tickets/" + ticket.getId() + "/";
        for (Action action : ticket.getAllowedActions()) {

            String actionName = action.toString().toLowerCase();
            doc.addLink(new Link(basePath + actionName, actionName));
        }
        doc.addLink(new Link(basePath + "watchers", "watchers"));
        doc.addLink(new Link(basePath + "assign", "assign"));
        doc.addLink(new Link(basePath + "attachments", "attach_file"));
        doc.addLink(new Link(basePath + "comments", "add_comment"));
        return doc;
    }

    private Document toWatchersDoc(Ticket ticket) {
        List<Document<String>> watcherDocs = new ArrayList<>();
        Document<List<Document<String>>> doc = new Document<>(watcherDocs);
        String basePath = "http://localhost:8080/tickets/" + ticket.getId() + "/";
        for (UserID watcher : ticket.getWatchers()) {
            Document<String> current = new Document<>(watcher.toString());
            watcherDocs.add(current);
            Link add_watcher = new Link(basePath + "watcher", "self");
        }
        return doc;
    }

    private TicketTO map(Ticket ticket) {
        TicketTO to = new TicketTO(ticket.getId(), ticket.getReporter(), ticket.getStatus());
        to.setTitle(ticket.getTitle());
        to.setDescription(ticket.getDescription());
        if (ticket.getAssignee() != null) {
            to.setAssignee(ticket.getAssignee().toString());
        }
        for (UserID watcher : ticket.getWatchers()) {
            to.addWatcher(watcher.toString());
        }
        return to;
    }



}