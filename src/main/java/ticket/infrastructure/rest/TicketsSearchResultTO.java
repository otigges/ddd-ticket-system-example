package ticket.infrastructure.rest;

import ticket.domain.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TicketsSearchResultTO extends Document<List<Ticket>> {

    private final List<Ticket> tickets = new ArrayList<>();

    public TicketsSearchResultTO(List<Ticket> tickets) {
        this.tickets.addAll(tickets);
    }

}

