package se.javagroup.projecttask.repository;

import org.springframework.data.repository.CrudRepository;
import se.javagroup.projecttask.repository.data.Team;

public interface TeamRepository extends CrudRepository<Team, Long> {
}
