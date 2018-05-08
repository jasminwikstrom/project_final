package se.javagroup.projecttask.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import se.javagroup.projecttask.repository.data.Team;

public interface TeamRepository extends JpaRepository<Team, Long> {
}
