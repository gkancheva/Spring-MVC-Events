package eventSystem.services;


import eventSystem.models.User;

public interface UserService {
    boolean authenticate(String username, String password);
    User findById(Long id);
    User findByUsername(String username);
    User create (User user);
    User edit (User user);
    void deleteUser (Long id);
}
