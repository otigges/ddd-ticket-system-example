package domain;

import stereotypes.Repository;

@Repository
public interface TicketRepository {

    Ticket get(TicketID id);

    void add(Ticket ticket);

    void update(Ticket ticket);

}
