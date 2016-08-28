package eventSystem.controllers;

import eventSystem.forms.User.LoginForm;
import eventSystem.forms.User.RegisterForm;
import eventSystem.models.User;
import eventSystem.services.NotificationService;
import eventSystem.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.Objects;

@Controller
public class UserController {
    @Autowired
    private UserService userServ;

    @Autowired
    private NotificationService notServ;

    @RequestMapping("/users/login")
    public String login(LoginForm loginForm) {
        return "users/login";
    }

    @RequestMapping(value = "/users/login", method = RequestMethod.POST)
    public String loginPage(@Valid LoginForm lf, BindingResult br, HttpSession httpSession) {
        if(br.hasErrors()) {
            notServ.addErrorMessage("Please fill the form correctly");
            return "users/login";
        }
        if(!userServ.authenticate(lf.getUsername(), lf.getPassword())) {
            notServ.addErrorMessage("Invalid login!");
            return "users/login";
        }
        notServ.addInfoMessage("Login successful!");
        httpSession.setAttribute("loggedUser", userServ.findByUsername(lf.getUsername()));
        return "redirect:/";
    }

    @RequestMapping("/users/register")
    public String registerPage(RegisterForm registerForm) {
        return "users/register";
    }

    @RequestMapping(value = "/users/register", method = RequestMethod.POST)
    public String registerPage(@Valid RegisterForm rf, BindingResult br, HttpSession httpSession) {
        if(br.hasErrors()) {
            notServ.addErrorMessage("Please fill the form correctly");
            return "users/register";
        }
        if(!Objects.equals(rf.getPassword(), rf.getRetypePassword())) {
            notServ.addErrorMessage("The password does not match. Please try again.");
            return "users/register";
        }
        User user = new User(rf.getUsername(), rf.getPassword(), rf.geteMail(), rf.getFullName(), "ROLE_USER");
        userServ.create(user);
        notServ.addInfoMessage("Register successful!");
        httpSession.setAttribute("loggedUser", user);
        return "redirect:/";
    }

    @RequestMapping(value = "/users/logout", method = RequestMethod.POST)
    public String logoutPage(HttpSession httpSession) {
        notServ.addInfoMessage("You have been successfully logged out!");
        httpSession.invalidate();
        return "redirect:/";
    }
}
