package ticket;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import ticket.application.TicketService;
import ticket.domain.*;
import ticket.domain.TicketFactory.TicketBuilder;
import ticket.adapters.persistence.InMemoryTicketRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class TicketStepdefs {

    private final TicketFactory factory = new ClientDemo.DemoFactory();
    private final InMemoryTicketRepository repository = new InMemoryTicketRepository();
    private final TicketService service = new TicketService(factory, repository);

    private TicketID lastTicketID;

    @Given("No tickets exist")
    public void noTicketsExist() {
        repository.clear();
    }

    @Given("a ticket in status {word}")
    public void aTicketInStatusStatus(String statusName) {
        TicketBuilder builder = factory.ticketBuilder(new TicketID(1),
                new UserID("Jane"), Status.valueOf(statusName));
        Ticket ticket = builder.build();
        repository.add(ticket);
        lastTicketID = ticket.getId();
    }

    @When("{word} creates new ticket")
    public void createNewTicket(String userName) {
        Ticket ticket = service.createTicket(new UserID(userName), "Some title.", "Some description.");
        lastTicketID = ticket.getId();
    }

    @Then("{word} is in the list of watchers")
    public void sheIsInTheListOfWatchers(String userName) {
        assertTrue(ticket().getWatchers().contains(new UserID(userName)));
    }

    @Then("ticket is in status {word}")
    public void ticketIsInStatus(String status) {
        assertEquals(Status.valueOf(status), ticket().getStatus());
    }

    @Then("{word} is an allowed action")
    public void actionIsAllowed(String action) {
        System.out.println(ticket().getAllowedActions());
        assertTrue(ticket().getAllowedActions().contains(Action.valueOf(action)));
    }

    @Then("{word} is not an allowed action")
    public void actionIsNotAllowed(String action) {
        assertFalse(ticket().getAllowedActions().contains(Action.valueOf(action)));
    }

    // --------------------

    private Ticket ticket() {
        Optional<Ticket> ticket = service.findTicket(lastTicketID);
        if (ticket.isPresent()) {
            return ticket.get();
        } else {
            throw new IllegalStateException("Ticket with this ID does not exist: " + lastTicketID);
        }
    }


}