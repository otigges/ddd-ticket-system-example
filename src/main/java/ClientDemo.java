import domain.*;

public class ClientDemo {

    private static final UserID JANE = new UserID("Jane");
    private static final UserID BOB = new UserID("Bob");

    private static int ticketIdGen = 1;

    private static TicketFactory ticketFactory = new TicketFactory(new DomainEventPublisher() {
        @Override
        public void publish(TicketEvent event) {
            System.out.println(event + "\n");
        }
    }, () -> new TicketID(ticketIdGen++) );



    public static void main(String[] args) throws IllegalStateTransitionException {

        // Jane creates new ticket

        Ticket ticket = ticketFactory.createNewTicket(JANE, "Wrong price calculation", "...");
        ticket.watch(JANE);
        ticket.assignTo(BOB);

        System.out.println(ticket);

        // Bob analyzes ticket

        ticket.apply(Action.START_PROGRESS);
        ticket.watch(BOB);
        ticket.addComment("Could reproduce and detected that...", BOB);

        // Bob solves ticket
        ticket.updateDescription("Root of problem is....");
        ticket.apply(Action.RESOLVE);
        ticket.apply(Action.CLOSE);

        System.out.println(ticket);

    }

}
