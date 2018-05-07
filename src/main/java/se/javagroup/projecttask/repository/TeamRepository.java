package se.javagroup.projecttask.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import se.javagroup.projecttask.repository.data.Team;

import java.util.List;

public interface TeamRepository extends CrudRepository<Team, Long> {

    @Query("SELECT t FROM Team t")
    List<Team> getAllTeams();
}
