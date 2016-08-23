package eventSystem.controllers;

import eventSystem.forms.Events.CreateNewEventForm;
import eventSystem.forms.Events.EditEventForm;
import eventSystem.models.Event;
import eventSystem.models.User;
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
        List<Event> allEvents = eventService.findOrdered();
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
        //ToDo: Take the user from the session
        return "events/create";
    }

    @RequestMapping(value = "/events/create", method = RequestMethod.POST)
    public String validateAndCreateEvent(@Valid CreateNewEventForm cf, BindingResult br) {
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
        event.setAuthor(userService.findById((long)7));
        event.setPublic(true);
        eventService.create(event);
        notServ.addInfoMessage("Event with id #" + event.getId() + " was successfully created.");
        return "redirect:/events";
    }

    @RequestMapping(value = "/events/delete/{id}", method = RequestMethod.GET)
    public String showDeleteEventPage(@PathVariable("id") Long id, Model m) {
        Event event = eventService.findById(id);
        if(event == null) {
            notServ.addErrorMessage("Cannot find event with id #" + id);
            return "redirect:/events";
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

    @RequestMapping(value = "/events/edit/{id}", method = RequestMethod.GET)
    public String showEditPage(@PathVariable ("id") Long id, Model m, EditEventForm editEventForm) {
        Event event = eventService.findById(id);
        if(event == null) {
            notServ.addErrorMessage("Cannot find event with id #" + id);
            return "redirect:/events";
        }
        m.addAttribute("event", event);
        editEventForm.setAuthor(event.getAuthor());
        editEventForm.setTitle(event.getTitle());
        editEventForm.setDescription(event.getDescription());
        editEventForm.setDate(event.getDate());
        editEventForm.setLocation(event.getLocation());
        return ("events/edit");
    }

    @RequestMapping(value = "events/edit/{id}", method = RequestMethod.POST)
    public String editEvent(@PathVariable ("id") Long id, @Valid EditEventForm ef, BindingResult br) {
        //TODO: 23.8.2016 г. : get author
        if(br.hasErrors()) {
            notServ.addErrorMessage("Please fill the form correctly");
            return "events/edit";
        }
        Event event = new Event();
        event.setId(id);
        event.setTitle(ef.getTitle());
        event.setDescription(ef.getDescription());
        event.setDate(ef.getDate());
        event.setLocation(ef.getLocation());
        event.setAuthor(userService.findById((long)7));
        event.setPublic(true);
        eventService.edit(event);
        notServ.addInfoMessage("Event with id #" + id + " was successfully modified.");
        return "redirect:/events";
    }
}
