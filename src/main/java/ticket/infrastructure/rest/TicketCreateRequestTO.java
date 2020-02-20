package ticket.infrastructure.rest;

import javax.validation.constraints.NotBlank;

public class TicketCreateRequestTO {

    @NotBlank
    private String reporter;
    @NotBlank
    private String title;
    private String description;

    public TicketCreateRequestTO() {
    }

    public TicketCreateRequestTO(String reporter, String title, String description) {
        this.reporter = reporter;
        this.title = title;
        this.description = description;
    }

    public String getReporter() {
        return reporter;
    }

    public void setReporter(String reporter) {
        this.reporter = reporter;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}

