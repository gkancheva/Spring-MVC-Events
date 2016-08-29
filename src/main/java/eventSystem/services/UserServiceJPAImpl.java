package eventSystem.services;

import eventSystem.models.User;
import eventSystem.repositories.UserRepository;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Primary
public class UserServiceJPAImpl implements UserService{
    @Autowired
    private UserRepository userRepo;

    @Override
    public boolean authenticate(String username, String password) {
        List<User> users = userRepo.findAll();
        Long id = checkIfUserExists(users, username);
        if(id != -1) {
            User user = userRepo.findOne(id);
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
    public Long checkIfUserExists(List<User> users, String username) {
        for (int i = 0; i < users.size(); i++) {
            if(users.get(i).getUsername().equals(username)) {
                return users.get(i).getId();
            }
        }
        return (long) -1;
    }
}
