package ticket.infrastructure.rest;

import ticket.domain.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TicketTO {

    private final List<Attachment> attachments = new ArrayList<>();
    private final List<Comment> comments = new ArrayList<>();
    private final Set<String> watchers = new HashSet<>();

    private String id;
    private String reporter;
    private String status;
    private String title;
    private String description;
    private String assignee;

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
}

