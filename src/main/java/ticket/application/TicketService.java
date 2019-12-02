package ticket.application;

import org.springframework.stereotype.Service;
import ticket.domain.*;

import java.util.Optional;

@Service
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

    public Optional<Ticket> findTicket(TicketID id) {
        return repository.get(id);
    }

    public void update(Ticket ticket) {
        repository.update(ticket);
    }

}
