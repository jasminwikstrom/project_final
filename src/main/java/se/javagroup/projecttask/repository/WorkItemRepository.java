package se.javagroup.projecttask.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import se.javagroup.projecttask.repository.data.WorkItem;

import javax.ws.rs.PathParam;
import java.util.Collection;

public interface WorkItemRepository extends JpaRepository<WorkItem, Long> {

    @Query("SELECT w FROM #{#entityName} w INNER JOIN w.user u WHERE u.id = :id")
    Collection<WorkItem> findWorkItemsByUserId(@PathParam("id") Long id);

}
