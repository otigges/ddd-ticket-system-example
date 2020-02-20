package ticket.domain;

import java.util.function.Predicate;

public class SearchCriteria implements Predicate<Ticket> {

    public static SearchCriteria any() {
        return new SearchCriteria(null, null, null, null);
    }

    public static SearchCriteria byStatus(Status status) {
        return new SearchCriteria(status, null, null, null);
    }

    public static SearchCriteria byReporter(UserID reporter) {
        return new SearchCriteria(null, reporter, null, null);
    }

    public static SearchCriteria byAssignee(UserID assignee) {
        return new SearchCriteria(null, null, assignee, null);
    }

    public static SearchCriteria byWatcher(UserID watcher) {
        return new SearchCriteria(null, null, null, watcher);
    }

    Status status;

    UserID reporter;

    UserID assignee;

    UserID watcher;

    public SearchCriteria(Status status, UserID reporter, UserID assignee, UserID watcher) {
        this.status = status;
        this.reporter = reporter;
        this.assignee = assignee;
        this.watcher = watcher;
    }

    public Status getStatus() {
        return status;
    }

    public UserID getReporter() {
        return reporter;
    }

    public UserID getAssignee() {
        return assignee;
    }

    public UserID getWatcher() {
        return watcher;
    }

    @Override
    public boolean test(Ticket ticket) {
        if (status != null && !status.equals(ticket.getStatus())) {
            return false;
        }
        if (reporter != null && !reporter.equals(ticket.getReporter())) {
            return false;
        }
        if (assignee != null && !assignee.equals(ticket.getReporter())) {
            return false;
        }
        if (watcher != null && !ticket.getWatchers().contains(watcher)) {
            return false;
        }
        return true;
    }

}
