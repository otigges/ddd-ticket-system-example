package ticket.domain;

import stereotypes.ValueObject;

import java.io.Serializable;

@ValueObject
public class Attachment implements Serializable {

    private final UserID author;

    private final String url;

    private final String description;

    public Attachment(UserID author, String url, String description) {
        this.author = author;
        this.url = url;
        this.description = description;
    }

    public UserID getAuthor() {
        return author;
    }

    public String getUrl() {
        return url;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return "Attachment{" +
                "author=" + author +
                ", url='" + url + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
