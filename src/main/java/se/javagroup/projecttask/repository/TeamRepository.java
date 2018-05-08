package se.javagroup.projecttask.repository;

<<<<<<< HEAD
import org.springframework.data.repository.CrudRepository;
import se.javagroup.projecttask.repository.data.Team;

public interface TeamRepository extends CrudRepository<Team, Long> {
=======
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import se.javagroup.projecttask.repository.data.Team;

import java.util.List;

public interface TeamRepository extends JpaRepository<Team, Long> {

    @Query("SELECT t FROM Team t")
    List<Team> getAllTeams();
>>>>>>> master
}
