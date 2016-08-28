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

import javax.servlet.http.HttpSession;
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

    @RequestMapping("/events")
    public String showAllEvents(Model m, HttpSession httpSession) {
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
        if(httpSession.getAttribute("loggedUser") != null) {
            User user = (User)httpSession.getAttribute("loggedUser");
            m.addAttribute("loggedUser", user);
        }
        return "events/index";
    }

    @RequestMapping("/events/view/{id}")
    public String showSingleEvent(@PathVariable("id") Long id, Model m, HttpSession httpSession) {
        Event event = eventService.findById(id);
        if(event == null) {
            notServ.addErrorMessage("Cannot find event with id #" + id);
            return "redirect:/";
        }
        m.addAttribute("event", event);
        if(httpSession.getAttribute("loggedUser") != null) {
            User user = (User)httpSession.getAttribute("loggedUser");
            m.addAttribute("loggedUser", user);
        }
        return "events/view";
    }

    @RequestMapping("/events/create")
    public String showCreateEventPage(CreateNewEventForm createNewEventForm, HttpSession httpSession, Model m) {
        if(httpSession.getAttribute("loggedUser") != null) {
            User user = (User)httpSession.getAttribute("loggedUser");
            m.addAttribute("loggedUser", user);
            return "events/create";
        }
        notServ.addErrorMessage("You should be logged in to create new event. Please login.");
        return "redirect:/users/login";
    }

    @RequestMapping(value = "/events/create", method = RequestMethod.POST)
    public String validateAndCreateEvent(@Valid CreateNewEventForm cf, BindingResult br, HttpSession httpSession) {
        //TODO: Change the type="text" with type="date" to show the calendar window
        if(httpSession.getAttribute("loggedUser") != null) {
            User user = (User) httpSession.getAttribute("loggedUser");
            if(br.hasErrors()) {
                notServ.addErrorMessage("Please fill the form correctly.");
                httpSession.setAttribute("loggedUser", user);
                return "events/create";
            }
            Event event = new Event();
            event.setTitle(cf.getTitle());
            event.setDescription(cf.getDescription());
            event.setDate(cf.getDate());
            event.setLocation(cf.getLocation());
            event.setAuthor(userService.findById(user.getId()));
            event.setPublic(true);
            eventService.create(event);
            notServ.addInfoMessage("Event with id #" + event.getId() + " was successfully created.");
            httpSession.setAttribute("loggedUser", user);
            return "redirect:/events";
        }
        notServ.addErrorMessage("An error occurred, please try again.");
        return "redirect:/events";
    }

    @RequestMapping(value = "/events/delete/{id}", method = RequestMethod.GET)
    public String showDeleteEventPage(@PathVariable("id") Long id, Model m, HttpSession httpSession) {
        if(httpSession.getAttribute("loggedUser") != null) {
            User user = (User)httpSession.getAttribute("loggedUser");
            m.addAttribute(user);
            Event event = eventService.findById(id);
            if(event == null) {
                notServ.addErrorMessage("Cannot find event with id #" + id);
                httpSession.setAttribute("loggedUser", user);
                m.addAttribute("loggedUser", user);
                return "redirect:/events";
            }
            m.addAttribute("event", event);
            httpSession.setAttribute("loggedUser", user);
            m.addAttribute("loggedUser", user);
            return "events/delete";
        }
        notServ.addErrorMessage("You are not allowed to delete events. Please login.");
        return "redirect:/users/login";
    }

    @RequestMapping(value = "events/delete/{id}", method = RequestMethod.POST)
    public String deleteEvent(@PathVariable("id") Long id, HttpSession httpSession, Model m) {
        if(httpSession.getAttribute("loggedUser") != null) {
            User user = (User)httpSession.getAttribute("loggedUser");
            eventService.deleteById(id);
            httpSession.setAttribute("loggedUser", user);
            notServ.addInfoMessage("Event with id #" + id + " was successfully deleted.");
            m.addAttribute("loggedUser", user);
            return ("redirect:/events");
        }
        notServ.addErrorMessage("You are not allowed to delete this event.");
        return "redirect:/users/login";
    }

    @RequestMapping(value = "/events/edit/{id}", method = RequestMethod.GET)
    public String showEditPage(@PathVariable ("id") Long id, Model m, EditEventForm editEventForm, HttpSession httpSession) {
        if(httpSession.getAttribute("loggedUser") != null) {
            User user = (User)httpSession.getAttribute("loggedUser");
            Event event = eventService.findById(id);
            if(event == null) {
                notServ.addErrorMessage("Cannot find event with id #" + id);
                httpSession.setAttribute("loggedUser", user);
                m.addAttribute("loggedUser", user);
                return "redirect:/events";
            }
            m.addAttribute("event", event);
            editEventForm.setTitle(event.getTitle());
            editEventForm.setDescription(event.getDescription());
            editEventForm.setLocation(event.getLocation());
            editEventForm.setDate(event.getDate());
            editEventForm.setAuthor(user);
            httpSession.setAttribute("loggedUser", user);
            m.addAttribute("loggedUser", user);
            return ("events/edit");
        }
        notServ.addErrorMessage("You are not allowed to edit the event. Please login");
        return "redirect:/users/login";
    }

    @RequestMapping(value = "events/edit/{id}", method = RequestMethod.POST)
    public String editEvent(@PathVariable ("id") Long id, @Valid EditEventForm ef, BindingResult br, HttpSession httpSession, Model m) {
        if(httpSession.getAttribute("loggedUser") != null) {
            User user = (User)httpSession.getAttribute("loggedUser");
            if(br.hasErrors()) {
                notServ.addErrorMessage("Please fill the form correctly");
                httpSession.setAttribute("loggedUser", user);
                m.addAttribute("loggedUser", user);
                return "events/edit";
            }
            Event event = new Event();
            event.setId(id);
            event.setTitle(ef.getTitle());
            event.setDescription(ef.getDescription());
            event.setDate(ef.getDate());
            event.setLocation(ef.getLocation());
            event.setAuthor(userService.findById(user.getId()));
            event.setPublic(true);
            eventService.edit(event);
            notServ.addInfoMessage("Event with id #" + id + " was successfully modified.");
            httpSession.setAttribute("loggedUser", user);
            m.addAttribute("loggedUser", user);
            return "redirect:/events";
        }
        notServ.addErrorMessage("You are not allowed to modify this event. Please login.");
        return "redirect:/users/login";
    }
}
