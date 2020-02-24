package ticket.infrastructure.persistence;

import ticket.domain.*;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class AbstractTicketRepository implements TicketRepository {

    @Override
    public SearchResult search(SearchCriteria criteria, PageRequest pageRequest) {

        long total = allTickets().filter(criteria).count();
        List<Ticket> tickets = allTickets().filter(criteria)
                .skip(pageRequest.getPageSize() * pageRequest.getPage())
                .limit(pageRequest.getPageSize())
                .collect(Collectors.toList());


        return new SearchResult(tickets, total, pageRequest.getPageSize(), pageRequest.getPage());

    }

    protected abstract Stream<Ticket> allTickets();

}
