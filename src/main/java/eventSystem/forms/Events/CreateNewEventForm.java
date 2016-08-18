package eventSystem.forms.Events;

import eventSystem.models.User;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;

public class CreateNewEventForm {
    @NotNull
    @Size(min = 10, max = 300, message = "Title of the event should be between 10 and 300 characters.")
    private String title;

    @NotNull
    @Size(min = 10, message = "The event description should be at least 10 characters.")
    private String description;

    @NotNull
    private Date date;

    @NotNull
    private String location;

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
}
