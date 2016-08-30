package eventSystem.repositories;

import eventSystem.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {


    User findByUsername(String username);

    @Query("SELECT u.username FROM User u")
    List<String> findAllUsernames();
}
