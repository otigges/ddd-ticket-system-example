package ticket.infrastructure.rest;

import ticket.domain.Ticket;

import java.util.ArrayList;
import java.util.List;

public class TicketsSearchResultTO extends Document<List<Ticket>> {

    private final List<TicketTO> tickets = new ArrayList<>();

    public TicketsSearchResultTO(List<Ticket> tickets) {
        tickets.forEach(t -> this.tickets.add(TicketTO.from(t)));
    }

}

