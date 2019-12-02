package ticket.infrastructure.persistence;

import ticket.domain.Ticket;
import ticket.domain.TicketID;
import ticket.domain.TicketRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class InMemoryTicketRepository implements TicketRepository {

    private final Map<TicketID, Ticket> storage = new HashMap<>();

    @Override
    public Optional<Ticket> get(TicketID id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public void add(Ticket ticket) {
        update(ticket);
    }

    @Override
    public void update(Ticket ticket) {
        storage.put(ticket.getId(), ticket);
    }
}
