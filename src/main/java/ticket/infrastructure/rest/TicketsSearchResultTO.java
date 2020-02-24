package ticket.infrastructure.rest;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import ticket.domain.SearchCriteria;
import ticket.domain.SearchResult;
import ticket.domain.Ticket;

import java.util.ArrayList;
import java.util.List;

@JsonPropertyOrder({"result", "totalElements", "page", "pageSize", "links"})
public class TicketsSearchResultTO extends Document<List<Ticket>> {

    private final List<TicketTO> result = new ArrayList<>();

    private long totalElements;

    private int pageSize;

    private int page;

    public TicketsSearchResultTO(SearchCriteria criteria, SearchResult searchResult, LinkBuilder linkBuilder) {
        if (linkBuilder != null) {
            searchResult.getTickets().forEach(t -> this.result.add(TicketTO.from(t, linkBuilder)));
            if (searchResult.hasNext()) {
                addLink(linkBuilder.linkToNextPage(criteria, searchResult), "next");
            }
            if (searchResult.hasPrevious()) {
                addLink(linkBuilder.linkToPreviousPage(criteria, searchResult), "previous");
            }
        } else {
            searchResult.getTickets().forEach(t -> this.result.add(TicketTO.from(t)));
        }
        this.totalElements = searchResult.getTotalElements();
        this.pageSize = searchResult.getPageSize();
        this.page = searchResult.getPage();
    }

    public TicketsSearchResultTO(SearchResult searchResult) {
        this(new SearchCriteria(), searchResult, null);
    }

    public List<TicketTO> getResult() {
        return result;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public int getPageSize() {
        return pageSize;
    }

    public int getPage() {
        return page;
    }
}

