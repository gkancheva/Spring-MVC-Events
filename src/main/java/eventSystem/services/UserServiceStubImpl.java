package eventSystem.services;

import eventSystem.models.User;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class UserServiceStubImpl implements UserService{
    private List<User> users = new ArrayList<User>() {{
        add(new User(1l, "Pesho", "Pesho123", "Pesho Peshev"));
        add(new User(2l, "Maria", "Maria123", "Maria Petrova"));
        add(new User(3l, "Gergana", "Gery123", "Gergana Kancheva"));
    }};


    @Override
    public boolean authenticate(String username, String password) {
        //TODO: check the password correctly with Spring Security
        return Objects.equals(username, password);
    }

    @Override
    public User findById(Long id) {
        return this.users.stream()
                .filter(e -> Objects.equals(e.getId(), id))
                .findFirst().orElse(null);
    }

    @Override
    public User findByUsername(String username) {
        return this.users.stream()
                .filter(u -> Objects.equals(u.getUsername(), username))
                .findFirst().orElse(null);
    }

    @Override
    public User create(User user) {
        user.setId(this.users.stream().mapToLong(u -> u.getId()).max().getAsLong() + 1);
        this.users.add(user);
        return user;
    }

    @Override
    public User edit(User user) {
        for (int i = 0; i < this.users.size(); i++) {
            if(Objects.equals(this.users.get(i).getId(), user.getId())) {
                this.users.set(i, user);
                return user;
            }
        }
        throw new RuntimeException("User not found: " + user.getId());
    }

    @Override
    public void deleteUser(Long id) {
        for (int i = 0; i < this.users.size(); i++) {
            if(Objects.equals(this.users.get(i).getId(), id)) {
                this.users.remove(i);
                return;
            }
        }
        throw new RuntimeException("User not found: " + id);
    }
}
