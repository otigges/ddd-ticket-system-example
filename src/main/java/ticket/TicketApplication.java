package ticket;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import ticket.domain.TicketFactory;
import ticket.domain.TicketID;
import ticket.domain.TicketRepository;
import ticket.infrastructure.persistence.InMemoryTicketRepository;

@SpringBootApplication
public class TicketApplication {

    private static int ticketIdGen = 1;

    public static void main(String[] args) {
        SpringApplication.run(TicketApplication.class, args);
    }

    @Bean
    public TicketFactory ticketFactory() {
        return new TicketFactory(
                event -> System.out.println(event + "\n"),
                () -> new TicketID(ticketIdGen++));
    }

    @Bean
    public TicketRepository ticketRepository() {
        return new InMemoryTicketRepository();
    }

}
