package ticket.infrastructure.rest;

import ticket.domain.Ticket;
import ticket.domain.UserID;

import java.util.HashSet;
import java.util.Set;

public class WatcherListTO {

    private final Set<WatcherTO> watchers = new HashSet<>();

    public static WatcherListTO from(Ticket ticket, LinkBuilder linkBuilder) {
        WatcherListTO list = new WatcherListTO();
        for (UserID watcher : ticket.getWatchers()) {
            Link link = Link.self(linkBuilder.linkToWatcher(ticket, watcher));
            link.addMethods("GET", "PUT", "DELETE");
            WatcherTO watcherTO = new WatcherTO(watcher.toString(), link);
            list.addWatcher(watcherTO);
        }
        return list;
    }

    public WatcherListTO() {

    }

    public Set<WatcherTO> getWatchers() {
        return watchers;
    }

    public void addWatcher(WatcherTO watcher) {
        this.watchers.add(watcher);
    }
}

