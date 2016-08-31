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
    private static String ERROR_DELETING_ADMIN = "You are trying to delete admin account.";
    private static String ROLE_ADMIN = "ROLE_ADMIN";
    private static String ROLE_USER = "ROLE_USER";
    private static Long ADMIN_ID = (long)1;

    @Autowired
    private UserService userService;

    @Autowired
    private EventService eventService;

    @Autowired
    private NotificationService notificationService;

    @RequestMapping(value = "/users")
    public String showUsers(Model m, HttpSession httpSession) {
        if(userService.isLoggedIn(httpSession)) {
            if(userService.isAdmin(httpSession)) {
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
        if(userService.isLoggedIn(httpSession)) {
            if(userService.isAdmin(httpSession)) {
                User user = userService.findById(id);
                if(user == null) {
                    notificationService.addErrorMessage(ERROR_NO_SUCH_USER + id);
                    return "/users/index";
                }
                if(user.getId() == 1) {
                    notificationService.addErrorMessage("You cannot modify admin user.");
                    return "redirect:/users";
                }
                m.addAttribute("user", user);
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
        if(userService.isLoggedIn(httpSession)) {
            if(userService.isAdmin(httpSession)) {
                User user = userService.findById(id);
                user.setRole(euf.getRole());
                userService.edit(user);
                notificationService.addInfoMessage("User with id #" + id + " has been successfully modified.");
                m.addAttribute("users", userService.findAll());
                return "/users/admin/index";
            }
        }
        notificationService.addErrorMessage(ERROR_UNAUTHORIZED_USER);
        return "redirect:/users/login";
    }

    @RequestMapping(value = "/users/delete/{id}", method = RequestMethod.GET)
    public String showDeleteUserPage(@PathVariable("id") Long id, Model m, HttpSession httpSession) {
        if(userService.isLoggedIn(httpSession)) {
            if(userService.isAdmin(httpSession)){
                User userToDelete = userService.findById(id);
                if(userToDelete.getId() == ADMIN_ID) {
                    notificationService.addErrorMessage(ERROR_DELETING_ADMIN);
                    return "redirect:/users";
                }
                if(userToDelete != null) {
                    m.addAttribute("user", userToDelete);
                    return "users/admin/delete";
                }
                notificationService.addErrorMessage(ERROR_NO_SUCH_USER);
                return "redirect:/users";
            }
            notificationService.addErrorMessage(ERROR_UNAUTHORIZED_USER);
            return "redirect:/";
        }
        notificationService.addErrorMessage(ERROR_USER_NOT_LOGGED_IN);
        return "redirect:/users/login";
    }

    @RequestMapping(value = "users/delete/{id}", method = RequestMethod.POST)
    public String deleteUser(@PathVariable("id") Long userId, HttpSession httpSession) {
        if(userService.isLoggedIn(httpSession)) {
            User admin = (User)httpSession.getAttribute(LOGGED_USER);
            if(userService.isAdmin(httpSession)) {
                if(admin.getId() == ADMIN_ID) {
                    notificationService.addErrorMessage(ERROR_DELETING_ADMIN);
                    return "redirect:/users";
                }
                if(userService.findById(userId).getEvents().size() > 0) {
                    List<Event> events = eventService.findEventsOfSpecUser(userId);
                    for (int i = 0; i < events.size(); i++) {
                        eventService.deleteById(events.get(i).getId());
                    }
                }
                userService.deleteById(userId);
                notificationService.addInfoMessage("User #" + userId + " has been successfully deleted!");
                return "redirect:/users";
            }
            notificationService.addErrorMessage(ERROR_UNAUTHORIZED_USER);
            return "redirect:/";
        }
        notificationService.addErrorMessage(ERROR_USER_NOT_LOGGED_IN);
        return "redirect:/users/login";
    }
}