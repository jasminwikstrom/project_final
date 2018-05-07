package se.javagroup.projecttask.repository;

import org.springframework.data.repository.CrudRepository;
import se.javagroup.projecttask.repository.data.User;

public interface UserRepository extends CrudRepository<User, Long> {
}
