//Repository
package se.javagroup.projecttask.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import se.javagroup.projecttask.repository.data.User;

public interface UserRepository extends JpaRepository<User, Long> {
}