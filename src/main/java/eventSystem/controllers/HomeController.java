package eventSystem.controllers;

import eventSystem.models.Event;
import eventSystem.services.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class HomeController {

    @Autowired
    private EventService eventService;

    @RequestMapping("/")
    public String index(Model m) {
        Date today = new Date();
        List<Event> upcomingEvents = eventService.findUpcoming();
        m.addAttribute("allUpcomingEvents", upcomingEvents);
        List<Event> upcoming3events = upcomingEvents.stream()
                .limit(5).collect(Collectors.toList());
        m.addAttribute("upcoming5events", upcoming3events);
        List<Event> past3Events = eventService.findPast().stream()
                .limit(3).collect(Collectors.toList());
        m.addAttribute("past3events", past3Events);
        return "index";
    }
}
