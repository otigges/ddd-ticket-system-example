package ticket.domain;

import stereotypes.ValueObject;

@ValueObject
public class Comment {

    private final String content;

    private final UserID author;

    public Comment(String content, UserID author) {
        this.content = content;
        this.author = author;
    }

    public String getContent() {
        return content;
    }

    public UserID getAuthor() {
        return author;
    }

    @Override
    public String toString() {
        return author + ": '" + content + '\'';
    }
}
