package se.javagroup.projecttask.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import se.javagroup.projecttask.repository.data.Issue;

public interface IssueRepository extends JpaRepository<Issue, Long> {
}
