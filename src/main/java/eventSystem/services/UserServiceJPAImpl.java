package eventSystem.services;

import eventSystem.models.User;
import eventSystem.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Primary
public class UserServiceJPAImpl implements UserService{
    @Autowired
    private UserRepository userRepo;

    @Override
    public boolean authenticate(String username, String password) {
        String pass_hash = BCrypt.hashpw(password, BCrypt.gensalt());
        List<User> users = userRepo.findAll();
        Long id = findIfUserExists(users, username);
        if(id != -1) {
            User user = userRepo.findOne(id);
            if(BCrypt.checkpw(password, user.getPasswordHash())) {
                return true;
            }
        }
        return false;
    }

    private Long findIfUserExists(List<User> users, String username) {
        for (int i = 0; i < users.size(); i++) {
            if(users.get(i).getUsername().equals(username)) {
                return users.get(i).getId();
            }
        }
        return (long) -1;
    }

    @Override
    public List<User> findAll() {
        return this.userRepo.findAll();
    }

    @Override
    public User findById(Long id) {
        return this.userRepo.findOne(id);
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
}
