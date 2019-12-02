package domain;

public interface DomainEventPublisher {

    void publish(TicketEvent event);
    
}
