package se.javagroup.projecttask.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import se.javagroup.projecttask.repository.data.User;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public interface UserRepository extends JpaRepository<User, Long> {

    @Query(value = "SELECT * FROM USER t where " +
            "(:firstname is null or t.first_name = :firstname) AND " +
            "(:lastname is null or t.last_name = :lastname)", nativeQuery = true)
    List<User> findAllByQuery(@Param("firstname") String firstName,
                              @Param("lastname") String lastName);

   /* @Query("SELECT * FROM TEAM t.users where t.name := name")
    List<User> findUsersInTeam(@Param("name") String name);*/

    User add(User user);

    Optional<User> get(Long id);


    Stream<User> getAll();

    User update(User user);

    Optional<User> delete(Long id);
}
