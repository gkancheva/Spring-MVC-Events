package eventSystem.services;

import eventSystem.models.Event;
import eventSystem.models.User;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class EventServiceStubImpl implements EventService{
    private List<Event> events = new ArrayList<Event>() {{
        add(new Event(1L, "Title 1st event", "Description 1st Event", new Date(2016-10-20), "Some address", new User(1l, "Pesho", "Pesho123", "Pesho Peshev"), true));
        add(new Event(1L, "Title 2nd event", "Description 2nd Event", new Date(2016-10-25), "Some address", new User(2l, "Maria", "Maria123", "Maria Petrova"), true));
    }};

    @Override
    public List<Event> findAll() {
        return this.events;
    }

    @Override
    public List<Event> findUpcoming5() {
        return this.events.stream()
                .sorted((a, b) -> a.getDate().compareTo(b.getDate()))
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
