package ticket.infrastructure.rest;


public class TicketUpdateRequestTO {

    private String title;
    private String description;
    private String assignee;

    public TicketUpdateRequestTO() {
    }

    public TicketUpdateRequestTO(String title, String description, String assignee) {
        this.title = title;
        this.description = description;
        this.assignee = assignee;
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

    public String getAssignee() {
        return assignee;
    }

    public void setAssignee(String assignee) {
        this.assignee = assignee;
    }

    public boolean titleChanged() {
        return this.title != null;
    }

    public boolean descriptionChanged() {
        return this.description != null;
    }

    public boolean assigneeChanged() {
        return this.assignee != null;
    }
}

