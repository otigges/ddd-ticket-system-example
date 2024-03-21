package ticket.adapters.rest.config;

import org.springframework.context.annotation.Bean;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import ticket.domain.TicketID;
import ticket.domain.UserID;

@Component
public class TicketSystemRestConfig {

    @Bean
    public TicketIdConverter ticketIdConverter(){
        return new TicketIdConverter();
    }

    @Bean
    public UserIdConverter userIdConverter(){
        return new UserIdConverter();
    }

    class TicketIdConverter implements Converter<String, TicketID> {

        @Override
        public TicketID convert(String source) {
            return new TicketID(Integer.parseInt(source));
        }

    }

    class UserIdConverter implements Converter<String, UserID> {

        @Override
        public UserID convert(String source) {
            return new UserID(source);
        }

    }

}
