//Repository
package se.javagroup.projecttask.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import se.javagroup.projecttask.repository.data.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    @Query(value =
            "SELECT * FROM USER u " +
                    "LEFT JOIN TEAM t on t.id=u.team_id " +
                    "WHERE (:firstname is null or u.first_name = :firstname) AND" +
                    "(:lastname is null or u.last_name = :lastname) AND " +
                    "(:username is null or u.username = :username) AND " +
                    "(:usernumber is null or u.user_number = :usernumber) AND " +
                    "(:teamname is null or t.name = :teamname)", nativeQuery = true)
    List<User> findAllByQuery(@Param("firstname") String firstName,
                              @Param("lastname") String lastName,
                              @Param("username") String username,
                              @Param("teamname") String teamname,
                              @Param("usernumber") String userNumber);

    Optional<User> findByUserNumber(Long userNumber);
}
