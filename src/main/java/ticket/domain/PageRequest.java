package ticket.domain;

public class PageRequest {

    private int page = 0;

    private int pageSize = 10;

    public PageRequest(int page, int pageSize) {
        this.page = page;
        this.pageSize = pageSize;
    }

    public PageRequest(int page) {
        this.page = page;
    }

    public PageRequest() {
    }

    public int getPageSize() {
        return pageSize;
    }

    public int getPage() {
        return page;
    }
}
