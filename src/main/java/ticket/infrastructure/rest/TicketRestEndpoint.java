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
import java.util.Optional;

@RestController
public class TicketRestEndpoint {


    public static final UserID JANE = new UserID("Jane");

    public static final String BASE_URI = "http://localhost:8080/tickets/";

    private TicketService ticketService;

    @Autowired
    public TicketRestEndpoint(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @GetMapping("/tickets")
    public ResponseEntity<?>  getServiceDocument(HttpServletRequest request) {
        ServiceInfo info = new ServiceInfo(request);

        Document<ServiceInfo> document = new Document<>(info);
        return new ResponseEntity(document, HttpStatus.OK);
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
        Ticket ticket = ticketService.createTicket(JANE, "Something is wrong.", "Some details.");
        ticket.watch(JANE);
        UriComponents uriComponents =
                b.path("/tickets/{id}").buildAndExpand(ticket.getId());

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(uriComponents.toUri());
        return new ResponseEntity<Void>(headers, HttpStatus.CREATED);
    }

    private Document<?> toDoc(Ticket ticket) {
        Document<TicketTO> doc = new Document<>(map(ticket));
        String basePath = BASE_URI + ticket.getId() + "/";
        for (Action action : ticket.getAllowedActions()) {

            String actionName = action.toString().toLowerCase();
            doc.addLink(new Link(basePath + actionName, actionName).addMethods("POST"));
        }
        doc.addLink(new Link(basePath + "watchers", "watchers").addMethods("GET", "POST"));
        doc.addLink(new Link(basePath + "assign", "assign").addMethods("POST"));
        doc.addLink(new Link(basePath + "attachments", "attach_file").addMethods("GET", "POST"));
        doc.addLink(new Link(basePath + "comments", "add_comment").addMethods("GET", "POST"));
        return doc;
    }

    private Document<WatcherListTO> toWatchersDoc(Ticket ticket) {
        String basePath = BASE_URI + ticket.getId() + "/";
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