package ticket.infrastructure.rest;

import com.fasterxml.jackson.annotation.JsonInclude;
import ticket.domain.*;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Transfer object for a ticket.
 */
public class TicketTO {

    private String id;
    private String reporter;
    private String status;
    private String title;
    private String description;
    private String assignee;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private final List<Link> links = new ArrayList<>();
    private final List<Attachment> attachments = new ArrayList<>();
    private final List<Comment> comments = new ArrayList<>();
    private final Set<String> watchers = new HashSet<>();

    public static TicketTO from(Ticket ticket) {
        TicketTO to = new TicketTO(ticket.getId(), ticket.getReporter(), ticket.getStatus());
        to.setTitle(ticket.getTitle());
        to.setDescription(ticket.getDescription());
        if (ticket.getAssignee() != null) {
            to.setAssignee(ticket.getAssignee().toString());
        }
        for (UserID watcher : ticket.getWatchers()) {
            to.addWatcher(watcher.toString());
        }
        return to;
    }

    public static TicketTO from(Ticket ticket, LinkBuilder linkBuilder) {
        TicketTO to = TicketTO.from(ticket);
        to.setSelfLink(linkBuilder.linkTo(ticket));
        return to;
    }

    public TicketTO(TicketID id, UserID reporter, Status status) {
        this.id = id.toString();
        this.reporter = reporter.toString();
        this.status = status.toString();
    }

    public String getId() {
        return id;
    }

    public String getReporter() {
        return reporter;
    }

    public void setReporter(String reporter) {
        this.reporter = reporter;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public List<Attachment> getAttachments() {
        return attachments;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public Set<String> getWatchers() {
        return watchers;
    }

    public void addWatcher(String watcher) {
        this.watchers.add(watcher);
    }

    public List<Link> getLinks() {
        return links;
    }

    protected void setSelfLink(URI selfLink) {
        this.links.add(Link.self(selfLink));
    }
}

