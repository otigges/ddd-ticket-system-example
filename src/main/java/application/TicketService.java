package application;

import domain.*;

public class TicketService {

    private final TicketFactory factory;

    private final TicketRepository repository;

    public TicketService(TicketFactory factory, TicketRepository repository) {
        this.factory = factory;
        this.repository = repository;
    }

    public Ticket createTicket(UserID author, String title, String description) {
        Ticket ticket = factory.createNewTicket(author, title, description);
        repository.add(ticket);
        return ticket;
    }

    public Ticket findTicket(TicketID id) {
        return repository.get(id);
    }










}
