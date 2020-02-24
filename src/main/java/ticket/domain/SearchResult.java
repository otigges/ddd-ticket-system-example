package ticket.domain;

import java.util.List;

import static java.util.Collections.unmodifiableList;

public class SearchResult {

    private final List<Ticket> tickets;

    private final long totalElements;

    private final int pageSize;

    private int page;

    public SearchResult(List<Ticket> tickets, long totalElements, int pageSize, int page) {
        this.tickets = unmodifiableList(tickets);
        this.totalElements = totalElements;
        this.pageSize = pageSize;
        this.page = page;
    }

    public List<Ticket> getTickets() {
        return tickets;
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

    public int getTotalPages() {
        if (pageSize == 0 || totalElements == 0) {
            return 0;
        }
        return (int) Math.ceil(totalElements / (double) pageSize);
    }

    public boolean hasNext() {
        return totalElements > (page + 1) * pageSize;
    }

    public boolean hasPrevious() {
        return page > 0;
    }
}
