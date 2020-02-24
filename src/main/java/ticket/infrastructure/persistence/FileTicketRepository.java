package ticket.infrastructure.persistence;

import ticket.domain.*;

import java.io.*;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class FileTicketRepository implements TicketRepository {

    private int nextId;

    private final Map<TicketID, Ticket> storage = new HashMap<>();

    private final File file;

    private final DomainEventPublisher publisher;

    public FileTicketRepository(DomainEventPublisher publisher) {
        this.publisher = publisher;
        this.file = new File("ticket-store.bak");
        load(file);
        nextId = highestId() +1;
    }

    @Override
    public Optional<Ticket> get(TicketID id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public List<Ticket> search(SearchCriteria criteria) {
        return storage.values().stream().filter(criteria).collect(Collectors.toList());
    }

    @Override
    public void add(Ticket ticket) {
        update(ticket);
    }

    @Override
    public void update(Ticket ticket) {
        storage.put(ticket.getId(), ticket);
        store(file);
    }

    @Override
    public TicketID next() {
        return new TicketID(nextId++);
    }

    public void clear() {
        storage.clear();
        file.delete();
    }

    private int highestId() {
        return storage.values().stream()
                .map(t -> t.getId().getInternalId())
                .max((a,b) -> a.compareTo(b))
                .orElse(0);
    }

    // internals - load and store to disk

    private void load(File file) {
        if (file.canRead()) {
            try (InputStream in = new FileInputStream(file)) {
                ObjectInputStream oin = new ObjectInputStream(in);
                Ticket[] tickets = (Ticket[]) oin.readObject();
                for (Ticket ticket : tickets) {
                    storage.put(ticket.getId(), ticket);
                    Field publisherField = Ticket.class.getDeclaredField("publisher");
                    publisherField.setAccessible(true);
                    publisherField.set(ticket, publisher);
                }
                oin.close();
            } catch (Exception e) {
                e.printStackTrace();
                file.delete();
            }
        }
    }

    private void store(File file) {
        try(OutputStream out = new FileOutputStream(file)) {
            ObjectOutputStream oout = new ObjectOutputStream(out);
            oout.writeObject(storage.values().toArray(new Ticket[storage.size()]));
            oout.close();
        } catch (IOException e) {
            throw new RuntimeException("Failed to write ticket store to file " + file, e);
        }
    }

}
