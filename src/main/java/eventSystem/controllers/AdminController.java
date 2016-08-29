package eventSystem.controllers;

import eventSystem.forms.user.EditUserForm;
import eventSystem.models.User;
import eventSystem.repositories.UserRepository;
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
    private static String ERROR_UNAUTHORIZED_USER = "You are not authorized to edit users. Please login.";
    private static String ERROR_NO_SUCH_USER = "Cannot find user with id #";

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NotificationService notificationService;

    @RequestMapping(value = "/users")
    public String showUsers(Model m, HttpSession httpSession) {
        if(httpSession.getAttribute(LOGGED_USER) != null) {
            User user = (User)httpSession.getAttribute(LOGGED_USER);
            if(user.getRole().equals("ROLE_ADMIN")) {
                m.addAttribute("users", userService.findAll());
                return "/users/admin/index";
            }
            notificationService.addErrorMessage("Unauthorized action.");
            return "redirect:/";
        } else {
            notificationService.addErrorMessage("You are not logged in. Please login.");
            return "redirect:/users/login";
        }
    }

    @RequestMapping(value = "/users/{id}")
    public String showEditUserPage(@PathVariable("id") Long id, Model m, EditUserForm euf, HttpSession httpSession) {
        if(httpSession.getAttribute(LOGGED_USER) != null) {
            User admin = (User)httpSession.getAttribute(LOGGED_USER);
            if(admin.getRole().equals("ROLE_ADMIN")) {
                User user = userRepository.findOne(id);
                if(user == null) {
                    notificationService.addErrorMessage(ERROR_NO_SUCH_USER + id);
                    return "/users/index";
                }
                m.addAttribute("user", user);
                m.addAttribute(LOGGED_USER, admin);
                List<String> roles = new ArrayList<>();
                roles.add("ROLE_ADMIN");
                roles.add("ROLE_USER");
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
            if(admin.getRole().equals("ROLE_ADMIN")) {
                User user = userRepository.findOne(id);
                user.setRole(euf.getRole());
                userService.edit(user);
                notificationService.addInfoMessage("User with id #" + id + " has been successfully modified.");
                m.addAttribute(LOGGED_USER, admin);
                m.addAttribute("users", userRepository.findAll());
                return "/users/admin/index";
            }
        }
        notificationService.addErrorMessage(ERROR_UNAUTHORIZED_USER);
        return "redirect:/users/login";
    }

}