//Repository
package se.javagroup.projecttask.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import se.javagroup.projecttask.repository.data.User;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {

    @Query(value =
            "SELECT * FROM USER u " +
                    "JOIN TEAM t on t.id=u.team_id " +
                    "WHERE (:firstname is null or u.first_name = :firstname) AND" +
                    "(:lastname is null or u.last_name = :lastname) AND " +
                    "(:username is null or u.username = :username) AND " +
                    "(:teamname is null or t.name = :teamname)", nativeQuery = true)
    List<User> findAllByQuery(@Param("firstname") String firstName,
                              @Param("lastname") String lastName,
                              @Param("username") String username,
                              @Param("teamname") String teamname);
}
