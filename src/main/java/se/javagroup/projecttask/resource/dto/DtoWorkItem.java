package se.javagroup.projecttask.resource.dto;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import se.javagroup.projecttask.repository.data.Issue;
import se.javagroup.projecttask.repository.data.User;
import se.javagroup.projecttask.repository.data.WorkItemStatus;

import javax.persistence.*;

public class DtoWorkItem {

    private Long id;
    private String description;

    private String workItemStatus;

    private Long workItemNumber;
    private User user;

    private Issue issue;

    public DtoWorkItem(String description, String workItemStatus, User user) {
        this.description = description;
        this.workItemStatus = workItemStatus;
        this.user = user;
    }

    public Long getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public String getWorkItemStatus() {
        return workItemStatus;
    }

    public Long getWorkItemNumber() {
        return workItemNumber;
    }

    public User getUser() {
        return user;
    }

    public Issue getIssue() {
        return issue;
    }

    public void setIssue(Issue issue) {
        this.issue = issue;
    }
}
