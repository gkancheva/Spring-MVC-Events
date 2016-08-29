package eventSystem.controllers;

import eventSystem.models.User;
import eventSystem.forms.user.LoginForm;
import eventSystem.forms.user.RegisterForm;
import eventSystem.repositories.UserRepository;
import eventSystem.services.NotificationService;
import eventSystem.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.Objects;

@Controller
public class UserController {
    private static String LOGGED_USER = "loggedUser";
    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private NotificationService notificationService;

    @RequestMapping("/users/login")
    public String login(LoginForm loginForm) {
        return "users/login";
    }

    @RequestMapping(value = "/users/login", method = RequestMethod.POST)
    public String loginPage(@Valid LoginForm lf, BindingResult br, HttpSession httpSession) {
        if(br.hasErrors()) {
            notificationService.addErrorMessage("Please fill the form correctly");
            return "users/login";
        }
        if(!userService.authenticate(lf.getUsername(), lf.getPassword())) {
            notificationService.addErrorMessage("Invalid login!");
            return "users/login";
        }
        notificationService.addInfoMessage("Login successful!");
        httpSession.setAttribute(LOGGED_USER, userService.findByUsername(lf.getUsername()));
        return "redirect:/";
    }

    @RequestMapping("/users/register")
    public String registerPage(RegisterForm registerForm) {
        return "users/register";
    }

    @RequestMapping(value = "/users/register", method = RequestMethod.POST)
    public String registerPage(@Valid RegisterForm rf, BindingResult br, HttpSession httpSession) {
        if(br.hasErrors()) {
            notificationService.addErrorMessage("Please fill the form correctly");
            return "users/register";
        }
        if(!Objects.equals(rf.getPassword(), rf.getRetypePassword())) {
            notificationService.addErrorMessage("The password does not match. Please try again.");
            return "users/register";
        }
        if(userService.checkIfUserExists(userRepo.findAll(), rf.getUsername()) != -1) {
            notificationService.addErrorMessage("User with the same username already exists, please try with another username.");
            return "users/register";
        }
        User user = new User(rf.getUsername(), rf.getPassword(), rf.geteMail(), rf.getFullName(), "ROLE_USER");
        userService.create(user);
        notificationService.addInfoMessage("Register successful!");
        httpSession.setAttribute(LOGGED_USER, user);
        return "redirect:/";
    }

    @RequestMapping(value = "/users/logout", method = RequestMethod.POST)
    public String logoutPage(HttpSession httpSession) {
        httpSession.invalidate();
        notificationService.addInfoMessage("You have been successfully logged out!");
        return "redirect:/";
    }
}
