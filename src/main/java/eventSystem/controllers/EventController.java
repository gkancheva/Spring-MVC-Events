package eventSystem.controllers;

import eventSystem.forms.Events.CreateNewEventForm;
import eventSystem.models.Event;
import eventSystem.services.EventService;
import eventSystem.services.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.validation.Valid;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class EventController {
    @Autowired
    private EventService eventService;

    @Autowired
    private NotificationService notServ;

    @RequestMapping("/events/view/{id}")
    public String view(@PathVariable("id") Long id, Model m) {
        Event event = eventService.findById(id);
        if(event == null) {
            notServ.addErrorMessage("Cannot find event with id #" + id);
            return "redirect:/";
        }
        m.addAttribute("event", event);
        return "events/view";
    }

    @RequestMapping("/events")
    public String eventsIndex(Model m) {
        Date today = new Date();
        List<Event> allEvents = eventService.findAll();
        List<Event> allUpcomingEvents = allEvents
                .stream()
                .filter(e -> e.getDate().after(today))
                .collect(Collectors.toList());
        List<Event> allPastEvents = allEvents
                .stream()
                .filter(e -> e.getDate().before(today))
                .collect(Collectors.toList());
        m.addAttribute("allUpcomingEvents", allUpcomingEvents);
        m.addAttribute("allPastEvents", allPastEvents);
        return "events/index";
    }

    @RequestMapping("/events/create")
    public String createEvent(CreateNewEventForm createNewEventForm) {
        return "events/create";
    }

    @RequestMapping(value = "/events/create", method = RequestMethod.POST)
    public String createEventPage(@Valid CreateNewEventForm cf, BindingResult br, Model m) {
        if(br.hasErrors()) {
            notServ.addErrorMessage("Please fill the form correctly");
            return "events/create";
        }

        return "events/create";
    }
}
