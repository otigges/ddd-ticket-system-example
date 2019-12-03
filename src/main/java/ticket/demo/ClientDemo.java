package ticket.demo;

import ticket.domain.*;

public class ClientDemo {

    private static final UserID JANE = new UserID("Jane");
    private static final UserID BOB = new UserID("Bob");

    public static void main(String[] args) throws IllegalStateTransitionException {

        // Jane creates new ticket

        Ticket ticket = new DemoFactory().createNewTicket(JANE, "Wrong price calculation", "...");
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
