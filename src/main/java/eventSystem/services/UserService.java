package eventSystem.services;

import eventSystem.models.User;

import java.util.List;

public interface UserService {
    boolean authenticate(String username, String password);
    List<User> findAll();
    User findById(Long id);
    User findByUsername(String username);
    User create (User user);
    User edit (User user);
    Long checkIfUserExists(List<User> users, String username);
    void deleteById(Long id);
}
