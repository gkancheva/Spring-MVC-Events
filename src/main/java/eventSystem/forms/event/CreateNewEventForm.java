package eventSystem.forms.event;

import eventSystem.models.User;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;

public class CreateNewEventForm {
    @NotNull
    @Size(min = 10, max = 300, message = "Title of the event should be between 10 and 300 characters.")
    private String title;

    @NotNull
    @Size(min = 50, message = "The event description should be at least 50 characters.")
    private String description;

    @NotNull(message = "The date may not be null.")
    @DateTimeFormat(pattern = "dd-MMM-yyyy")
    private Date date;

    @NotNull
    private String location;

    private User author;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }
}
