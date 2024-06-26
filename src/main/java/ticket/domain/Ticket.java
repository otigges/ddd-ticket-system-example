package ticket.domain;

import stereotypes.Aggregate;
import stereotypes.AggregateId;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * The main domain object of the ticket system.
 */
@Aggregate
public class Ticket implements Serializable {

    private final List<Attachment> attachments = new ArrayList<>();
    private final List<Comment> comments = new ArrayList<>();
    private final Set<UserID> watchers = new HashSet<>();

    @AggregateId
    private final TicketID id;


    private transient final DomainEventPublisher publisher;
    private final StateMachine stateMachine;
    private final UserID reporter;
    private String title;
    private String description;
    private UserID assignee;

    Ticket(DomainEventPublisher publisher, TicketID id, Status status, String title, String description, UserID reporter, UserID assignee) {
        this.publisher = publisher;
        this.id = id;
        this.stateMachine = new StateMachine(status);
        this.title = title;
        this.description = description;
        this.reporter = reporter;
        this.assignee = assignee;
    }

    public Status getStatus() {
        return stateMachine.getCurrentState();
    }

    public Set<Action> getAllowedActions() {
        return stateMachine.getAllowedActions();
    }

    public void apply(Action action) throws IllegalStateTransitionException {
        Status status = stateMachine.apply(action);
        if (Status.CLOSED == status) {
            ticketClosed();
        }
    }

    public TicketID getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void updateTitle(String title) {
        this.title = title;
        ticketChanged();
    }

    public String getDescription() {
        return description;
    }

    public void updateDescription(String description) {
        this.description = description;
        ticketChanged();
    }

    public List<Attachment> getAttachments() {
        return attachments;
    }

    public void addAttachment(Attachment attachment) {
        this.attachments.add(attachment);
        ticketChanged();
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void addComment(String comment, UserID author) {
        this.comments.add(new Comment(comment, author));
        ticketChanged();
    }

    public Set<UserID> getWatchers() {
        return watchers;
    }

    public void watch(UserID userID) {
        watchers.add(userID);
    }

    public void unwatch(UserID userID) {
        watchers.remove(userID);
    }

    public UserID getReporter() {
        return reporter;
    }

    public UserID getAssignee() {
        return assignee;
    }

    public void assignTo(UserID assignee) {
        this.assignee = assignee;
        ticketChanged();
    }

    private void ticketChanged() {
        publisher.publish(new TicketChanged(id, "Ticket has changed."));
    }

    private void ticketClosed() {
        publisher.publish(new TicketClosed(id, "Ticket has been closed."));
    }

    @Override
    public String toString() {
        StringBuilder sb =  new StringBuilder().append("Ticket ")
                .append('#').append(id)
                .append(" '").append(title).append('\'')
                .append("\n\t description='").append(description).append('\'')
                .append("\n\t reporter=").append(reporter)
                .append("\n\t assignee=").append(assignee)
                .append("\n\t state=").append(stateMachine.getCurrentState())
                .append("\n\t watchers=").append(watchers);
        if (!comments.isEmpty()) {
            sb .append("\n\t comments=").append(comments);
        }
        if (!attachments.isEmpty()) {
            sb.append("\n\t attachments=").append(attachments);
        }
        sb.append("\n");
        return sb.toString();
    }


}
