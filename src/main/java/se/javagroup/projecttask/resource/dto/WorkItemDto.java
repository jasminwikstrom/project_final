package se.javagroup.projecttask.resource.dto;

import se.javagroup.projecttask.repository.data.Issue;
import se.javagroup.projecttask.repository.data.User;

public class WorkItemDto {
    private Long id;
    private String description;
    private String workItemStatus;
    private Long workItemNumber;
    private User user;
    private Issue issue;

    public WorkItemDto(String description, String workItemStatus, User user) {
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
