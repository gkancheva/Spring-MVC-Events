package eventSystem.controllers;

import eventSystem.forms.LoginForm;
import eventSystem.forms.RegisterForm;
import eventSystem.services.NotificationService;
import eventSystem.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.validation.Valid;

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
    public String loginPage(@Valid LoginForm lf, BindingResult br, Model m) {
        if(br.hasErrors()) {
            notServ.addErrorMessage("Please fill the form correctly");
            return "users/login";
        }
        if(!userServ.authenticate(lf.getUsername(), lf.getPassword())) {
            notServ.addErrorMessage("Invalid login!");
            return "users/login";
        }
        notServ.addInfoMessage("Login successful!");
        m.addAttribute("user", userServ.findByUsername(lf.getUsername()));
        return "redirect:/";
    }

    @RequestMapping("/users/register")
    public String registerPage(RegisterForm registerForm) {
        return "users/register";
    }

    @RequestMapping(value = "/users/login", method = RequestMethod.POST)
    public String registerPage(@Valid RegisterForm rf, BindingResult br, Model m) {
        if(br.hasErrors()) {
            notServ.addErrorMessage("Please fill the form correctly");
            return "users/register";
        }

        notServ.addInfoMessage("Register successful!");
        return "redirect:/";
    }
}
