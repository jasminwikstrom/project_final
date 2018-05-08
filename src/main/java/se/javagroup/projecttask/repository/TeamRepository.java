package se.javagroup.projecttask.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import se.javagroup.projecttask.repository.data.Team;

import java.util.List;

public interface TeamRepository extends JpaRepository<Team, Long> {

    @Query("SELECT t FROM Team t")
    List<Team> getAllTeams();
}
