package ticket.domain;

import stereotypes.Repository;

import java.util.Optional;

@Repository
public interface TicketRepository {

    Optional<Ticket> get(TicketID id);

    void add(Ticket ticket);

    void update(Ticket ticket);

}
