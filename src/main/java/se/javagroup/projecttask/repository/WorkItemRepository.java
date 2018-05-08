package se.javagroup.projecttask.repository;

import org.springframework.data.repository.CrudRepository;
import se.javagroup.projecttask.repository.data.WorkItem;

public interface WorkItemRepository  extends CrudRepository<WorkItem, Long>{

}
