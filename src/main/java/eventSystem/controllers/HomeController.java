package eventSystem.controllers;

import eventSystem.models.Event;
import eventSystem.models.User;
import eventSystem.services.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class HomeController {
    private static String LOGGED_USER = "loggedUser";
    private static String UPCOMING_EVENTS = "allUpcomingEvents";
    private static String UPCOMING_5 = "upcoming5events";
    private static String UPCOMING_3 = "past3events";

    @Autowired
    private EventService eventService;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String index(Model m, HttpSession httpSession) {
        List<Event> upcomingEvents = eventService.findUpcoming();
        m.addAttribute(UPCOMING_EVENTS, upcomingEvents);
        List<Event> upcoming5events = upcomingEvents.stream()
                .limit(5).collect(Collectors.toList());
        m.addAttribute(UPCOMING_5, upcoming5events);
        List<Event> past3Events = eventService.findPast().stream()
                .limit(3).collect(Collectors.toList());
        m.addAttribute(UPCOMING_3, past3Events);
        User user = (User)httpSession.getAttribute(LOGGED_USER);
        m.addAttribute(LOGGED_USER, user);
        return "index";
    }
}
