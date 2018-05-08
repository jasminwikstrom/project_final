package se.javagroup.projecttask.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import se.javagroup.projecttask.repository.data.Team;

<<<<<<< HEAD
=======
import java.util.List;

public interface TeamRepository extends CrudRepository<Team, Long> {
>>>>>>> issue
public interface TeamRepository extends JpaRepository<Team, Long> {
}
