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

    /* ##########   EXERCISE No. 1 ############
     * Implement update of tickets:
     * - Title and description may be changed
     * - ID and reporter may not be changed
     * - Only valid state transitions may be allowed
     */

    public ResponseEntity<?> updateTicket() {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

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