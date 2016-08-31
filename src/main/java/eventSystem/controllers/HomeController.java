package eventSystem.controllers;

import eventSystem.models.Event;
import eventSystem.services.EventService;
import eventSystem.services.UserService;
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
    private static String UPCOMING_EVENTS = "upcoming10events";
    private static String UPCOMING_5 = "upcoming5events";
    private static String UPCOMING_3 = "past3events";

    @Autowired
    private EventService eventService;

    @Autowired
    private UserService userService;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String index(Model m, HttpSession httpSession) {
        List<Event> upcoming10;
        List<Event> upcoming5events;
        List<Event> past3Events;
        if(userService.isLoggedIn(httpSession)) {
            upcoming10 = eventService
                    .findUpcoming().stream()
                    .limit(10).collect(Collectors.toList());
            upcoming5events = upcoming10.stream()
                    .limit(5).collect(Collectors.toList());
            past3Events = eventService.findPast().stream()
                    .limit(3).collect(Collectors.toList());
        } else {
            upcoming10 = eventService
                    .findUpcoming().stream()
                    .filter(Event::isPublic)
                    .limit(10).collect(Collectors.toList());
            upcoming5events = upcoming10.stream()
                    .limit(5).collect(Collectors.toList());
            past3Events = eventService.findPast().stream()
                    .filter(Event::isPublic)
                    .limit(3).collect(Collectors.toList());
        }
        m.addAttribute(UPCOMING_3, past3Events);
        m.addAttribute(UPCOMING_5, upcoming5events);
        m.addAttribute(UPCOMING_EVENTS, upcoming10);
        return "index";
    }
}
