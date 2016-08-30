package eventSystem.controllers;

import eventSystem.forms.user.EditUserForm;
import eventSystem.models.Event;
import eventSystem.models.User;
import eventSystem.services.EventService;
import eventSystem.services.NotificationService;
import eventSystem.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

@Controller
public class AdminController {
    private static String LOGGED_USER = "loggedUser";
    private static String ERROR_UNAUTHORIZED_USER = "You are not allowed to access this data.";
    private static String ERROR_NO_SUCH_USER = "Cannot find user with id #";
    private static String ERROR_USER_NOT_LOGGED_IN = "You are not logged in. Please login.";
    private static String ROLE_ADMIN = "ROLE_ADMIN";
    private static String ROLE_USER = "ROLE_USER";

    @Autowired
    private UserService userService;

    @Autowired
    private EventService eventService;

    @Autowired
    private NotificationService notificationService;

    @RequestMapping(value = "/users")
    public String showUsers(Model m, HttpSession httpSession) {
        if(httpSession.getAttribute(LOGGED_USER) != null) {
            User user = (User)httpSession.getAttribute(LOGGED_USER);
            if(user.getRole().equals(ROLE_ADMIN)) {
                m.addAttribute("users", userService.findAll());
                return "/users/admin/index";
            }
            notificationService.addErrorMessage(ERROR_UNAUTHORIZED_USER);
            return "redirect:/";
        } else {
            notificationService.addErrorMessage(ERROR_USER_NOT_LOGGED_IN);
            return "redirect:/users/login";
        }
    }

    @RequestMapping(value = "/users/{id}", method = RequestMethod.GET)
    public String showEditUserPage(@PathVariable("id") Long id, Model m, HttpSession httpSession) {
        if(httpSession.getAttribute(LOGGED_USER) != null) {
            User admin = (User)httpSession.getAttribute(LOGGED_USER);
            if(admin.getRole().equals(ROLE_ADMIN)) {
                User user = userService.findById(id);
                if(user == null) {
                    notificationService.addErrorMessage(ERROR_NO_SUCH_USER + id);
                    return "/users/index";
                }
                m.addAttribute("user", user);
                m.addAttribute(LOGGED_USER, admin);
                List<String> roles = new ArrayList<>();
                roles.add(ROLE_ADMIN);
                roles.add(ROLE_USER);
                m.addAttribute("roles", roles);
                return "/users/admin/edit";
            }
        }
        notificationService.addErrorMessage(ERROR_UNAUTHORIZED_USER);
        return "redirect:/users/login";
    }

    @RequestMapping(value = "/users/{id}", method = RequestMethod.POST)
    public String editUser(@PathVariable("id") Long id, Model m, EditUserForm euf, HttpSession httpSession) {
        if(httpSession.getAttribute(LOGGED_USER) != null) {
            User admin = (User)httpSession.getAttribute(LOGGED_USER);
            if(admin.getRole().equals(ROLE_ADMIN)) {
                User user = userService.findById(id);
                user.setRole(euf.getRole());
                userService.edit(user);
                notificationService.addInfoMessage("User with id #" + id + " has been successfully modified.");
                m.addAttribute(LOGGED_USER, admin);
                m.addAttribute("users", userService.findAll());
                return "/users/admin/index";
            }
        }
        notificationService.addErrorMessage(ERROR_UNAUTHORIZED_USER);
        return "redirect:/users/login";
    }

    @RequestMapping(value = "/users/delete/{id}", method = RequestMethod.GET)
    public String showDeleteUserPage(@PathVariable("id") Long id, Model m, HttpSession httpSession) {
        if(httpSession.getAttribute(LOGGED_USER) != null) {
            User admin = (User)httpSession.getAttribute(LOGGED_USER);
            if(admin.getRole().equals(ROLE_ADMIN)){
                User userToDelete = userService.findById(id);
                if(userToDelete == null) {
                    notificationService.addErrorMessage(ERROR_NO_SUCH_USER);
                    return "redirect:/users";
                }
                m.addAttribute("user", userToDelete);
                return "users/admin/delete";
            }
            notificationService.addErrorMessage(ERROR_UNAUTHORIZED_USER);
            return "redirect:/";
        }
        notificationService.addErrorMessage(ERROR_USER_NOT_LOGGED_IN);
        return "redirect:/users/login";
    }

    @RequestMapping(value = "users/delete/{id}", method = RequestMethod.POST)
    public String deleteUser(@PathVariable("id") Long id, HttpSession httpSession) {
        if(httpSession.getAttribute(LOGGED_USER) != null) {
            User admin = (User)httpSession.getAttribute(LOGGED_USER);
            if(admin.getRole().equals(ROLE_ADMIN)) {
                if(admin.getId() == id) {
                    notificationService.addErrorMessage("You are trying to delete your own account.");
                    return "redirect:/users";
                }
                if(userService.findById(id).getEvents().size() > 0) {
                    List<Event> events = eventService.findEventsOfSpecUser(id);
                    for (int i = 0; i < events.size(); i++) {
                        eventService.deleteById(events.get(i).getId());
                    }
                }
                userService.deleteById(id);
                notificationService.addInfoMessage("User #" + id + " has been successfully deleted!");
                return "redirect:/users";
            }
            notificationService.addErrorMessage(ERROR_UNAUTHORIZED_USER);
            return "redirect:/";
        }
        notificationService.addErrorMessage(ERROR_USER_NOT_LOGGED_IN);
        return "redirect:/users/login";
    }
}