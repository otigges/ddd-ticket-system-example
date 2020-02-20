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

public class InMemoryTicketRepository implements TicketRepository {

    private int nextId = 1;

    private final Map<TicketID, Ticket> storage = new HashMap<>();

    @Override
    public Optional<Ticket> get(TicketID id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public List<Ticket> search(SearchCriteria criteria) {
        return storage.values().stream().filter(criteria).collect(Collectors.toList());
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
