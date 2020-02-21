package ticket.application;

import org.springframework.stereotype.Service;
import ticket.domain.*;

import java.util.List;
import java.util.Optional;

@Service
public class TicketService {

    private final TicketFactory factory;

    private final TicketRepository repository;

    public TicketService(TicketFactory factory, TicketRepository repository) {
        this.factory = factory;
        this.repository = repository;
    }

    public Ticket createTicket(UserID reporter, String title, String description, UserID... watcher) {
        Ticket ticket = factory.createNewTicket(reporter, title, description);
        for (UserID w : watcher) {
            ticket.watch(w);
        }
        repository.add(ticket);
        return ticket;
    }

    public Optional<Ticket> findTicket(TicketID id) {
        return repository.get(id);
    }

    public List<Ticket> searchTicket(SearchCriteria criteria) {
        return repository.search(criteria);
    }

    public void update(Ticket ticket) {
        repository.update(ticket);
    }

}
