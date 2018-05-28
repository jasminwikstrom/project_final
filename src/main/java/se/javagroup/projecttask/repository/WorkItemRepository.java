package se.javagroup.projecttask.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import se.javagroup.projecttask.repository.data.WorkItem;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

public interface WorkItemRepository extends JpaRepository<WorkItem, Long> {
    @Query("SELECT w FROM #{#entityName} w INNER JOIN w.user u WHERE u.id = :id")
    Collection<WorkItem> findWorkItemsByUserId(@Param("id") Long id);

    @Query(value =
            "SELECT * FROM  WORK_ITEM w " +
                    "LEFT JOIN USER u ON w.user_id=u.id " +
                    "LEFT JOIN ISSUE i ON i.work_item_id = w.id " +
                    "WHERE (:status is null or w.work_item_status = :status) AND " +
                    "(:issue is null or i.id is not NULL) AND " +
                    "(:text is null or w.description like %:text%) AND " +
                    "(:fromDate is null or w.date >= :fromDate) AND " +
                    "(:toDate is null or w.date <= :toDate) AND " +
                    "(:userid is null or w.user_id = :userid)", nativeQuery = true)
    List<WorkItem> findAllWorkItemsByQuery(@Param("status") String status,
                                           @Param("issue") Boolean issue,
                                           @Param("text") String text,
                                           @Param("userid") String userid,
                                           @Param("fromDate") LocalDate from,
                                           @Param("toDate") LocalDate to,
                                           Pageable pageable);
}
