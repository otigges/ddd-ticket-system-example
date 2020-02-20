package ticket.domain;

import stereotypes.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TicketRepository extends TicketIdGenerator {

    Optional<Ticket> get(TicketID id);

    List<Ticket> search(SearchCriteria criteria);

    void add(Ticket ticket);

    void update(Ticket ticket);

}
