package eventSystem.services;

import eventSystem.models.User;

import javax.servlet.http.HttpSession;
import java.util.List;

public interface UserService {
    boolean authenticate(String username, String password);
    List<User> findAll();
    List<String> findAllUsernames();
    User findById(Long id);
    User findByUsername(String username);
    User create (User user);
    User edit (User user);
    void deleteById(Long id);
    boolean isAdmin(HttpSession httpSession);
    boolean isLoggedIn(HttpSession httpSession);
}
