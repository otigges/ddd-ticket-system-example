package ticket.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.Assertions;

import java.util.Stack;

import static org.junit.jupiter.api.Assertions.*;


public class TicketTest {

    private final UserID JOHN = new UserID();

    private int ticketIdGen = 1;

    private Stack<TicketEvent> EVENT_LOG = new Stack<>();

    private TicketFactory ticketFactory = new TicketFactory(new DomainEventPublisher() {
        @Override
        public void publish(TicketEvent event) {
            System.out.println(event);
            EVENT_LOG.push(event);
        }
    }, () -> new TicketID(ticketIdGen++) );

    @Test()
    public void canOnlyReopenTicketsThatAreResolvedOrClosedOrArchived() throws IllegalStateTransitionException {
        Ticket ticket = ticketFactory.createNewTicket(JOHN, "title", "desc");

        assertEquals(Status.NEW, ticket.getStatus());

        assertFalse(ticket.getAllowedActions().contains(Action.REOPEN));

        Assertions.assertThrows(IllegalStateTransitionException.class, () -> ticket.apply(Action.REOPEN));

        ticket.apply(Action.START_PROGRESS);

        assertEquals(Status.IN_PROGRESS, ticket.getStatus());

        assertFalse(ticket.getAllowedActions().contains(Action.REOPEN));

        Assertions.assertThrows(IllegalStateTransitionException.class, () -> ticket.apply(Action.REOPEN));

        ticket.apply(Action.RESOLVE);

        assertEquals(Status.RESOLVED, ticket.getStatus());

        assertTrue(ticket.getAllowedActions().contains(Action.REOPEN));

        ticket.apply(Action.REOPEN);

        assertEquals(Status.OPEN, ticket.getStatus());

    }

    @Test()
    public void eventsSentOnTicketClose() throws IllegalStateTransitionException {
        Ticket ticket = ticketFactory.createNewTicket(JOHN, "title", "desc");

        ticket.apply(Action.START_PROGRESS);
        ticket.apply(Action.RESOLVE);

        assertTrue(EVENT_LOG.empty());

        ticket.apply(Action.CLOSE);

        assertEquals(1, EVENT_LOG.size());

        TicketEvent event = EVENT_LOG.peek();

        assertEquals(TicketClosed.TYPE, event.getType());
    }

    @BeforeEach
    public void setUp() {
        EVENT_LOG.clear();
    }


}
