package eventSystem.repositories;

import eventSystem.models.Event;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventRepository extends JpaRepository <Event, Long> {
    @Query("SELECT e FROM Event e LEFT JOIN FETCH e.author ORDER BY e.date")
    List<Event> findOrdered();
}