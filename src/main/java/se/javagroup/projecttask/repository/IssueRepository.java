package se.javagroup.projecttask.repository;

import org.springframework.data.repository.CrudRepository;
import se.javagroup.projecttask.repository.data.Issue;

public interface IssueRepository extends CrudRepository<Issue, Long> {
}
