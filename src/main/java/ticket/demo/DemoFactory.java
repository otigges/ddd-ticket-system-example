package ticket.demo;

import ticket.domain.*;

public class DemoFactory extends TicketFactory {

    private static int sequenceGen = 0;

    public DemoFactory() {
        super(new DomainEventPublisher() {
            @Override
            public void publish(TicketEvent event) {
                System.out.println(event);
            }
        }, new TicketIdGenerator() {
            @Override
            public TicketID next() {
                return new TicketID(++sequenceGen);
            }
        });
    }


}
