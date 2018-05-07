package se.javagroup.projecttask.repository;

import org.springframework.data.repository.CrudRepository;
import se.javagroup.projecttask.repository.data.User;

import java.util.Optional;
import java.util.stream.Stream;

public interface UserRepository extends CrudRepository<User, Long> {

    User add(User user);

    Optional<User> get(Long id);

    Stream<User> getAll();

    User update(User user);

    Optional<User> delete(Long id);
}
