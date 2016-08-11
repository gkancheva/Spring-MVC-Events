package eventSystem.controllers;

import eventSystem.models.Event;
import eventSystem.services.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.stream.Collectors;

@Controller
public class HomeController {

    @Autowired
    private EventService eventService;

    @RequestMapping("/")
    public String index(Model m) {
        List<Event> upcoming5events = eventService.findUpcoming5();
        m.addAttribute("upcoming5events", upcoming5events);
        List<Event> upcoming3events = upcoming5events.stream()
                .limit(3).collect(Collectors.toList());
        m.addAttribute("upcoming3events", upcoming3events);
        List<Event> past3Events = eventService.findPast5Events().stream()
                .limit(3).collect(Collectors.toList());
        m.addAttribute("past3events", past3Events);
        return "index";
    }
}
