package eventSystem.services;

import eventSystem.models.User;
import eventSystem.repositories.UserRepository;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.List;

@Service
@Primary
public class UserServiceJPAImpl implements UserService{
    private static String LOGGED_USER = "loggedUser";
    private static String ROLE_ADMIN = "ROLE_ADMIN";

    @Autowired
    private UserRepository userRepo;

    @Override
    public boolean authenticate(String username, String password) {
        if((userRepo.findByUsername(username)) != null) {
            User user = userRepo.findByUsername(username);
            if(BCrypt.checkpw(password, user.getPasswordHash())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public List<User> findAll() {
        return this.userRepo.findAll();
    }

    @Override
    public List<String> findAllUsernames() {
        return this.userRepo.findAllUsernames();
    }

    @Override
    public User findById(Long id) {
        return this.userRepo.findOne(id);
    }

    @Override
    public User findByUsername(String username) {
        return userRepo.findByUsername(username);
    }

    @Override
    public User create(User user) {
        String pass_hash = BCrypt.hashpw(user.getPasswordHash(), BCrypt.gensalt());
        user.setPasswordHash(pass_hash);
        return this.userRepo.save(user);
    }

    @Override
    public User edit(User user) {
        return this.userRepo.save(user);
    }

    @Override
    public void deleteById(Long id) {
        this.userRepo.delete(id);
    }

    @Override
    public boolean isAdmin(HttpSession httpSession) {
        User user = (User)httpSession.getAttribute(LOGGED_USER);
        if(user.getRole().equals(ROLE_ADMIN)) {
            return true;
        }
        return false;
    }

    @Override
    public boolean isLoggedIn(HttpSession httpSession) {
        if(httpSession.getAttribute(LOGGED_USER) != null) {
            return true;
        }
        return false;
    }
}
