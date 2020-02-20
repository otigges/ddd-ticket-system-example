package ticket;

import org.keycloak.adapters.springboot.KeycloakSpringBootConfigResolver;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import ticket.domain.TicketFactory;
import ticket.domain.TicketID;
import ticket.domain.TicketRepository;
import ticket.infrastructure.persistence.FileTicketRepository;

@SpringBootApplication
public class TicketApplication {

    private TicketRepository repo = new FileTicketRepository();

    public static void main(String[] args) {
        SpringApplication.run(TicketApplication.class, args);
    }

    @Bean
    public TicketFactory ticketFactory() {
        return new TicketFactory(
                event -> System.out.println(event + "\n"),
                repo);
    }

    @Bean
    public TicketRepository ticketRepository() {
        return repo;
    }

    @Bean
    public KeycloakSpringBootConfigResolver keycloakConfigResolver() {
        return new KeycloakSpringBootConfigResolver();
    }

}
