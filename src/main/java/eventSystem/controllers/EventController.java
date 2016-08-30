package eventSystem.controllers;

import eventSystem.forms.event.CreateNewEventForm;
import eventSystem.forms.event.EditEventForm;
import eventSystem.models.Event;
import eventSystem.models.User;
import eventSystem.services.EventService;
import eventSystem.services.NotificationService;
import eventSystem.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class EventController {
    private static String LOGGED_USER = "loggedUser";
    private static String ERROR_NO_SUCH_EVENT_ID = "Cannot find event with id #";
    private static String ERROR_USER_NOT_LOGGED_IN = "You should be logged in to create, modify or delete event. Please login.";
    private static String ERROR_UNAUTHORIZED_ACCESS = "You are not allowed to access this data.";
    private static String ERROR_FORM = "Please fill the form correctly.";
    private static String ROLE_ADMIN = "ROLE_ADMIN";
    private static String UPCOMING_EVENTS = "allUpcomingEvents";
    private static String PAST_EVENTS = "allPastEvents";

    @Autowired
    private UserService userService;

    @Autowired
    private EventService eventService;

    @Autowired
    private NotificationService notificationService;

    @RequestMapping("/events")
    public String showAllEvents(Model m, HttpSession httpSession) {
        List<Event> upcomingEvents = eventService.findUpcoming();
        List<Event> pastEvents = eventService.findPast();
        m.addAttribute(UPCOMING_EVENTS, upcomingEvents);
        m.addAttribute(PAST_EVENTS, pastEvents);
        if(httpSession.getAttribute(LOGGED_USER) != null) {
            User user = (User)httpSession.getAttribute(LOGGED_USER);
            m.addAttribute(LOGGED_USER, user);
        }
        return "events/index";
    }

    @RequestMapping("/events/{userId}")
    public String showEventsFromSpecificUser(@PathVariable("userId") Long userId, Model m, HttpSession httpSession) {
        if(httpSession.getAttribute(LOGGED_USER) != null) {
            User admin = (User)httpSession.getAttribute(LOGGED_USER);
            if(admin.getRole().equals(ROLE_ADMIN)) {
                List<Event> upcomingEvents = eventService
                        .findEventsOfSpecUser(userId).stream()
                        .filter(e -> e.getDate().after(new Date()))
                        .collect(Collectors.toList());
                List<Event> pastEvents = eventService
                        .findEventsOfSpecUser(userId).stream()
                        .filter(e -> e.getDate().before(new Date()))
                        .collect(Collectors.toList());
                m.addAttribute(UPCOMING_EVENTS, upcomingEvents);
                m.addAttribute(PAST_EVENTS, pastEvents);
                return "/events/index";
            }
            notificationService.addErrorMessage(ERROR_UNAUTHORIZED_ACCESS);
            return "redirect:/";
        }
        notificationService.addErrorMessage(ERROR_USER_NOT_LOGGED_IN);
        return "redirect:/users/login";
    }

    @RequestMapping("/events/view/{id}")
    public String showSingleEvent(@PathVariable("id") Long id, Model m, HttpSession httpSession) {
        Event event = eventService.findById(id);
        if(event == null) {
            notificationService.addErrorMessage(ERROR_NO_SUCH_EVENT_ID + id);
            return "redirect:/";
        }
        m.addAttribute("event", event);
        if(httpSession.getAttribute(LOGGED_USER) != null) {
            User user = (User)httpSession.getAttribute(LOGGED_USER);
            m.addAttribute(LOGGED_USER, user);
        }
        return "events/view";
    }

    @RequestMapping("/events/create")
    public String showCreateEventPage(CreateNewEventForm createNewEventForm, HttpSession httpSession, Model m) {
        if(httpSession.getAttribute(LOGGED_USER) != null) {
            User user = (User)httpSession.getAttribute(LOGGED_USER);
            m.addAttribute(LOGGED_USER, user);
            return "events/create";
        }
        notificationService.addErrorMessage(ERROR_USER_NOT_LOGGED_IN);
        return "redirect:/users/login";
    }

    @RequestMapping(value = "/events/create", method = RequestMethod.POST)
    public String validateAndCreateEvent(@Valid CreateNewEventForm cf, BindingResult br, HttpSession httpSession) {
        //TODO: Change the type="text" with type="date" to show the calendar window
        if(httpSession.getAttribute(LOGGED_USER) != null) {
            User user = (User) httpSession.getAttribute(LOGGED_USER);
            if(br.hasErrors()) {
                notificationService.addErrorMessage(ERROR_FORM);
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
            notificationService.addInfoMessage("Event with id #" + event.getId() + " has been successfully created.");
            return "redirect:/events";
        }
        notificationService.addErrorMessage(ERROR_USER_NOT_LOGGED_IN);
        return "redirect:/events";
    }

    @RequestMapping(value = "/events/delete/{id}", method = RequestMethod.GET)
    public String showDeleteEventPage(@PathVariable("id") Long id, Model m, HttpSession httpSession) {
        if(httpSession.getAttribute(LOGGED_USER) != null) {
            User user = (User)httpSession.getAttribute(LOGGED_USER);
            Event event = eventService.findById(id);
            if(event == null) {
                notificationService.addErrorMessage(ERROR_NO_SUCH_EVENT_ID + id);
                return "redirect:/events";
            }
            m.addAttribute("event", event);
            m.addAttribute(LOGGED_USER, user);
            return "events/delete";
        }
        notificationService.addErrorMessage(ERROR_USER_NOT_LOGGED_IN);
        return "redirect:/users/login";
    }

    @RequestMapping(value = "events/delete/{id}", method = RequestMethod.POST)
    public String deleteEvent(@PathVariable("id") Long id, HttpSession httpSession) {
        if(httpSession.getAttribute(LOGGED_USER) != null) {
            eventService.deleteById(id);
            notificationService.addInfoMessage("Event with id #" + id + " has been successfully deleted.");
            return ("redirect:/events");
        }
        notificationService.addErrorMessage(ERROR_USER_NOT_LOGGED_IN);
        return "redirect:/users/login";
    }

    @RequestMapping(value = "/events/edit/{id}", method = RequestMethod.GET)
    public String showEditPage(@PathVariable ("id") Long id, Model m, EditEventForm ef, HttpSession httpSession) {
        if(httpSession.getAttribute(LOGGED_USER) != null) {
            User user = (User)httpSession.getAttribute(LOGGED_USER);
            Event event = eventService.findById(id);
            if(event == null) {
                notificationService.addErrorMessage(ERROR_NO_SUCH_EVENT_ID + id);
                return "redirect:/events";
            }
            ef.setTitle(event.getTitle());
            ef.setDescription(event.getDescription());
            ef.setLocation(event.getLocation());
            ef.setDate(event.getDate());
            ef.setUsername(event.getAuthor().getUsername());
            if(user.getRole().equals(ROLE_ADMIN)) {
                m.addAttribute("usernames", userService.findAllUsernames());
            }
            m.addAttribute("event", event);
            return ("events/edit");
        }
        notificationService.addErrorMessage(ERROR_USER_NOT_LOGGED_IN);
        return "redirect:/users/login";
    }

    @RequestMapping(value = "events/edit/{id}", method = RequestMethod.POST)
    public String editEvent(@PathVariable ("id") Long eventId, @Valid EditEventForm ef, BindingResult br, HttpSession httpSession, Model m) {
        if(httpSession.getAttribute(LOGGED_USER) != null) {
            User user = (User)httpSession.getAttribute(LOGGED_USER);
            if(br.hasErrors()) {
                notificationService.addErrorMessage(ERROR_FORM);
                return "events/edit";
            }
            Event event = new Event();
            event.setId(eventId);
            event.setTitle(ef.getTitle());
            event.setDescription(ef.getDescription());
            event.setDate(ef.getDate());
            event.setLocation(ef.getLocation());
            if(user.getRole().equals(ROLE_ADMIN)) {
                User currentAuthor = userService.findByUsername(ef.getUsername());
                event.setAuthor(currentAuthor);
            } else {
                event.setAuthor(user);
            }
            event.setPublic(true);
            eventService.edit(event);
            notificationService.addInfoMessage("Event with id #" + eventId + " has been successfully modified.");
            m.addAttribute(LOGGED_USER, user);
            return "redirect:/events";
        }
        notificationService.addErrorMessage(ERROR_USER_NOT_LOGGED_IN);
        return "redirect:/users/login";
    }
}
