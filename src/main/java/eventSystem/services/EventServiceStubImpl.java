package eventSystem.services;

import eventSystem.models.Event;
import eventSystem.models.User;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class EventServiceStubImpl implements EventService{
    //TODO: check why the date is not printed correctly; Date class - deprecated
    private List<Event> events = new ArrayList<Event>() {{
        add(new Event(1L, "Title 1st event", "Description 1st Event", new Date((2016 - 1900), (10 - 1), 20), "Some address", new User(1l, "Pesho", "Pesho123", "pesho@peshev.com","Pesho Peshev"), true));
        add(new Event(2L, "Title 2nd event", "Description 2nd Event", new Date((2016 - 1900), (12 - 1), 20), "Some bar", new User(2l, "Maria", "Maria123", "maria@maria.com","Maria Petrova"), true));
        add(new Event(3L, "Title 3rd event", "Description 3rd Event", new Date((2016 - 1900), (6 - 1), 15), "Some location", new User(3l, "Ger", "Gery123", "gery@gery.com","Gergana Kancheva"), true));
        add(new Event(4L, "Title 4nd event", "Description 4rd Event", new Date((2017 - 1900), (3 - 1), 2), "Some bar", new User(4l, "Maria1", "Maria123", "maria@maria.com","Maria Petrova"), true));
        add(new Event(5L, "Title 5th event", "Description 5th Event", new Date((2016 - 1900), (6 - 1), 13), "Some location", new User(5l, "Gera", "Gery123", "gergana@gergana.com","Gergana Kancheva"), true));
    }};

    @Override
    public List<Event> findAll() {
        return this.events;
    }

    @Override
    public List<Event> findOrdered() {
        return this.events.stream()
                .sorted((a, b) -> a.getDate().compareTo(b.getDate()))
                .collect(Collectors.toList());
    }

    @Override
    public List<Event> findUpcoming() {
        return this.events.stream()
                .sorted((a, b) -> a.getDate().compareTo(b.getDate()))
                .filter(e -> e.getDate().after(new Date()))
                .limit(5).collect(Collectors.toList());
    }

    @Override
    public List<Event> findPast() {
        return this.events.stream()
                .sorted((a, b) -> b.getDate().compareTo(a.getDate()))
                .filter(e -> e.getDate().before(new Date()))
                .limit(5).collect(Collectors.toList());
    }

    @Override
    public Event findById(Long id) {
        return this.events.stream()
                .filter(e -> Objects.equals(e.getId(), id))
                .findFirst().orElse(null);
    }

    @Override
    public Event create(Event event) {
        event.setId(this.events.stream().mapToLong(e -> e.getId()).max().getAsLong() + 1);
        this.events.add(event);
        return event;
    }

    @Override
    public Event edit(Event event) {
        for (int i = 0; i < this.events.size(); i++) {
            if(Objects.equals(this.events.get(i).getId(), event.getId())) {
                this.events.set(i, event);
                return event;
            }
        }
        throw new RuntimeException("Event not found: " + event.getId());
    }

    @Override
    public void deleteById(Long id) {
        for (int i = 0; i < this.events.size(); i++) {
            if(Objects.equals(this.events.get(i).getId(), id)) {
                this.events.remove(i);
                return;
            }
        }
        throw new RuntimeException("Event not found: " + id);
    }
}
