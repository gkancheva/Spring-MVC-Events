package eventSystem.controllers;

import eventSystem.forms.user.LoginForm;
import eventSystem.forms.user.RegisterForm;
import eventSystem.models.User;
import eventSystem.services.NotificationService;
import eventSystem.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.Objects;

@Controller
public class UserController {
    private static String LOGGED_USER = "loggedUser";
    private static String ROLE_USER = "ROLE_USER";
    private static String ERROR_FORM = "Please fill the form correctly";
    private static String ERROR_INVALID_LOGIN = "Invalid login!";
    private static String ERROR_PASSWORD_MATCH = "The password does not match. Please try again.";
    private static String ERROR_USERNAME_UNAVAILABLE = "User with the same username already exists, please try with another username.";
    private static String SUCCESS_LOGIN = "Login successful!";
    private static String SUCCESS_REGISTER = "Register successful!";
    private static String SUCCESS_LOGOUT = "You have been successfully logged out!";

    @Autowired
    private UserService userService;

    @Autowired
    private NotificationService notificationService;

    @RequestMapping("/users/login")
    public String login(LoginForm loginForm) {
        return "users/login";
    }

    @RequestMapping(value = "/users/login", method = RequestMethod.POST)
    public String loginPage(@Valid LoginForm lf, BindingResult br, HttpSession httpSession) {
        if(br.hasErrors()) {
            notificationService.addErrorMessage(ERROR_FORM);
            return "users/login";
        }
        if(!userService.authenticate(lf.getUsername(), lf.getPassword())) {
            notificationService.addErrorMessage(ERROR_INVALID_LOGIN);
            return "users/login";
        }
        notificationService.addInfoMessage(SUCCESS_LOGIN);
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
            notificationService.addErrorMessage(ERROR_FORM);
            return "users/register";
        }
        if(!Objects.equals(rf.getPassword(), rf.getRetypePassword())) {
            notificationService.addErrorMessage(ERROR_PASSWORD_MATCH);
            return "users/register";
        }
        if((userService.findByUsername(rf.getUsername())) != null) {
            notificationService.addErrorMessage(ERROR_USERNAME_UNAVAILABLE);
            return "users/register";
        }
        User user = new User(rf.getUsername(), rf.getPassword(), rf.geteMail(), rf.getFullName(), ROLE_USER);
        userService.create(user);
        notificationService.addInfoMessage(SUCCESS_REGISTER);
        httpSession.setAttribute(LOGGED_USER, user);
        return "redirect:/";
    }

    @RequestMapping(value = "/users/logout", method = RequestMethod.POST)
    public String logoutPage(HttpSession httpSession) {
        httpSession.invalidate();
        notificationService.addInfoMessage(SUCCESS_LOGOUT);
        return "redirect:/";
    }
}
