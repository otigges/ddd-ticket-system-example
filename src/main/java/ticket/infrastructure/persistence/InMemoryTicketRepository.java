package ticket.infrastructure.persistence;

import ticket.domain.SearchCriteria;
import ticket.domain.Ticket;
import ticket.domain.TicketID;
import ticket.domain.TicketRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class InMemoryTicketRepository extends AbstractTicketRepository {

    private int nextId = 1;

    private final Map<TicketID, Ticket> storage = new HashMap<>();

    @Override
    public Optional<Ticket> get(TicketID id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    protected Stream<Ticket> allTickets() {
        return storage.values().stream();
    }

    @Override
    public void add(Ticket ticket) {
        update(ticket);
    }

    @Override
    public void update(Ticket ticket) {
        storage.put(ticket.getId(), ticket);
    }

    public void clear() {
        storage.clear();
    }

    @Override
    public TicketID next() {
        return new TicketID(nextId++);
    }
}
