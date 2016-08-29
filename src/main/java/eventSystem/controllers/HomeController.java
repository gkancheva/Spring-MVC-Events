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

    @Autowired
    private EventService eventService;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String index(Model m, HttpSession httpSession) {
        List<Event> upcomingEvents = eventService.findUpcoming();
        m.addAttribute("allUpcomingEvents", upcomingEvents);
        List<Event> upcoming5events = upcomingEvents.stream()
                .limit(5).collect(Collectors.toList());
        m.addAttribute("upcoming5events", upcoming5events);
        List<Event> past3Events = eventService.findPast().stream()
                .limit(3).collect(Collectors.toList());
        m.addAttribute("past3events", past3Events);
        User user = (User)httpSession.getAttribute("loggedUser");
        m.addAttribute("loggedUser", user);
        return "index";
    }
}
