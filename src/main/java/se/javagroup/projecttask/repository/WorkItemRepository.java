package se.javagroup.projecttask.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import se.javagroup.projecttask.repository.data.WorkItem;

<<<<<<< HEAD
public interface WorkItemRepository  extends CrudRepository<WorkItem, Long>{

=======
public interface WorkItemRepository extends JpaRepository<WorkItem, Long> {
>>>>>>> master
}
