package ticket;

import ticket.domain.*;

public class ClientDemo {

    private static final UserID JANE = new UserID("Jane");
    private static final UserID BOB = new UserID("Bob");

    public static void main(String[] args) throws IllegalStateTransitionException {

        // Jane creates new ticket

        Ticket ticket = new DemoFactory()
                .createNewTicket(JANE, "Wrong price calculation", "...");
        ticket.watch(JANE);
        ticket.assignTo(BOB);

        info(ticket);

        // Bob analyzes ticket

        ticket.watch(BOB);
        ticket.apply(Action.START_PROGRESS);
        ticket.addComment("Could reproduce and detected that...", BOB);
        ticket.addAttachment(new Attachment(BOB, "http://cms.example.com/4711", "Stacktrace"));

        info(ticket);

        // Bob solves ticket
        ticket.updateDescription("Wrong price calculation has root cause ....");
        ticket.apply(Action.RESOLVE);
        ticket.apply(Action.CLOSE);
        ticket.unwatch(BOB);

        info(ticket);

    }

    private static void info(Object msg) {
        System.out.println(msg);
    }

    public static class DemoFactory extends TicketFactory {

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
}
