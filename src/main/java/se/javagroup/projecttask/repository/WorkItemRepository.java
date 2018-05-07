package se.javagroup.projecttask.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import se.javagroup.projecttask.repository.data.WorkItem;

public interface WorkItemRepository extends JpaRepository<WorkItem, Long> {
}
