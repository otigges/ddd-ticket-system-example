package domain;

public class TicketFactory {

    private final DomainEventPublisher publisher;
    private final TicketIdGenerator idGenerator;

    public TicketFactory(DomainEventPublisher publisher, TicketIdGenerator idGenerator) {
        this.publisher = publisher;
        this.idGenerator = idGenerator;
    }

    public Ticket createNewTicket(UserID reporter, String title, String description) {
        Ticket ticket = ticketBuilder(idGenerator.next(), reporter, Status.NEW)
                .withTitle(title)
                .withDescription(description)
                .build();
        publisher.publish(new TicketCreated("Created new ticket."));
        return ticket;
    }

    public TicketBuilder ticketBuilder(TicketID id, UserID reporter, Status status) {
        return new TicketBuilder(id, reporter, status);
    }

    public class TicketBuilder {

        private final TicketID id;
        private final UserID reporter;
        private UserID assignee;
        private String title;
        private String description;
        private final Status status;

        TicketBuilder(TicketID id, UserID reporter, Status status) {
            this.id = id;
            this.reporter = reporter;
            this.status = status;
        }

        public TicketBuilder withAssignee(UserID assignee) {
            this.assignee = assignee;
            return this;
        }

        public TicketBuilder withTitle(String title) {
            this.title = title;
            return this;
        }

        public TicketBuilder withDescription(String description) {
            this.description = description;
            return this;
        }

        public Ticket build() {
            return new Ticket(publisher, id, status, title, description, reporter, assignee);
        }

    }

}
