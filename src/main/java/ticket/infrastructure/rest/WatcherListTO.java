package ticket.infrastructure.rest;

import ticket.domain.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class WatcherListTO {

    private final Set<WatcherTO> watchers = new HashSet<>();

    public WatcherListTO() {

    }

    public Set<WatcherTO> getWatchers() {
        return watchers;
    }

    public void addWatcher(WatcherTO watcher) {
        this.watchers.add(watcher);
    }
}

