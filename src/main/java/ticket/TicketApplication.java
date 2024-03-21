package ticket;

import org.keycloak.adapters.springboot.KeycloakSpringBootConfigResolver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import ticket.domain.DomainEventPublisher;
import ticket.domain.TicketEvent;
import ticket.domain.TicketFactory;
import ticket.domain.TicketRepository;
import ticket.adapters.persistence.FileTicketRepository;
import ticket.adapters.rest.LinkBuilder;

import java.net.URI;

@SpringBootApplication
public class TicketApplication {

    private DomainEventPublisher publisher = new DomainEventPublisher() {
        @Override
        public void publish(TicketEvent event) {
            System.out.println(event + "\n");
        }
    };

    private TicketRepository repo = new FileTicketRepository(publisher);

    @Value("${ticket-system.api.base-uri}")
    private URI apiBaseUri;

    public static void main(String[] args) {
        SpringApplication.run(TicketApplication.class, args);
    }

    @Bean
    public TicketFactory ticketFactory() {
        return new TicketFactory(publisher, repo);
    }

    @Bean
    public TicketRepository ticketRepository() {
        return repo;
    }

    @Bean
    public KeycloakSpringBootConfigResolver keycloakConfigResolver() {
        return new KeycloakSpringBootConfigResolver();
    }

    @Bean
    public LinkBuilder uriComponentsBuilder() {
        return new LinkBuilder(apiBaseUri);
    }

}
