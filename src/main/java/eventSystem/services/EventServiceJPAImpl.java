package eventSystem.services;

import eventSystem.models.Event;
import eventSystem.repositories.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Primary
public class EventServiceJPAImpl implements EventService{
    @Autowired
    private EventRepository eventRepo;

    @Override
    public List<Event> findAll() {
        return this.eventRepo.findAll();
    }

    @Override
    public List<Event> findOrdered() {
        return this.eventRepo.findOrdered();
    }

    @Override
    public List<Event> findUpcoming() {
        return this.eventRepo.findOrdered()
                .stream().filter(x -> x.getDate().after(new Date()))
                .collect(Collectors.toList());
    }

    @Override
    public List<Event> findPast() {
        return this.eventRepo.findOrdered()
                .stream().filter(x -> x.getDate().before(new Date()))
                .collect(Collectors.toList());
    }

    @Override
    public List<Event> findEventsOfSpecUser(Long userId) {
        return this.eventRepo.findEventsOfSpecUser(userId);
    }

    @Override
    public Event findById(Long id) {
        return this.eventRepo.findOne(id);
    }

    @Override
    public Event create(Event event) {
        return this.eventRepo.save(event);
    }

    @Override
    public Event edit(Event event) {
        return this.eventRepo.save(event);
    }

    @Override
    public void deleteById(Long id) {
        this.eventRepo.delete(id);
    }
}
