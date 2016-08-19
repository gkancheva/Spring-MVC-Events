package eventSystem.controllers;

import eventSystem.forms.Events.CreateNewEventForm;
import eventSystem.models.Event;
import eventSystem.services.EventService;
import eventSystem.services.NotificationService;
import eventSystem.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.beans.PropertyEditorSupport;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class EventController {

    @Autowired
    private UserService userService;


    @Autowired
    private EventService eventService;

    @Autowired
    private NotificationService notServ;

    @RequestMapping("/events/view/{id}")
    public String showSingleEvent(@PathVariable("id") Long id, Model m) {
        Event event = eventService.findById(id);
        if(event == null) {
            notServ.addErrorMessage("Cannot find event with id #" + id);
            return "redirect:/";
        }
        m.addAttribute("event", event);
        return "events/view";
    }

    @RequestMapping("/events")
    public String showAllEvents(Model m) {
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
    public String showCreateEventPage(CreateNewEventForm createNewEventForm) {
        return "events/create";
    }

    @RequestMapping(value = "/events/create", method = RequestMethod.POST)
    public String validateAndCreateEvent(@Valid CreateNewEventForm cf, BindingResult br, Model m) {
        //TODO: Get user id and setAuthor to the event when creating
        //TODO: Change the type="text" with type="date" to show the calendar window
        if(br.hasErrors()) {
            notServ.addErrorMessage("Please fill the form correctly");
            return "events/create";
        }
        Event event = new Event();
        event.setTitle(cf.getTitle());
        event.setDescription(cf.getDescription());
        event.setDate(cf.getDate());
        event.setLocation(cf.getLocation());
        eventService.create(event);
        return "redirect:/events";
    }

    @RequestMapping(value = "/events/delete/{id}", method = RequestMethod.GET)
    public String showDeleteEventPage(@PathVariable("id") Long id, Model m) {
        Event event = eventService.findById(id);
        if(event == null) {
            notServ.addErrorMessage("Cannot find event with id #" + id);
            return "redirect:/";
        }
        m.addAttribute("event", event);
        return "events/delete";
    }

    @RequestMapping(value = "events/delete/{id}", method = RequestMethod.POST)
    public String deleteEvent(@PathVariable("id") Long id) {
        eventService.deleteById(id);
        notServ.addInfoMessage("Event with id #" + id + " was successfully deleted.");
        return ("redirect:/events");
    }
}
