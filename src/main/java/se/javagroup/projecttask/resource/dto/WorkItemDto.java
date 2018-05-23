package se.javagroup.projecttask.resource.dto;

import se.javagroup.projecttask.repository.data.User;

public class WorkItemDto {
    private String description;
    private String workItemStatus;
    private User user;

    public WorkItemDto(String description, String workItemStatus, User user) {
        this.description = description;
        this.workItemStatus = workItemStatus;
        this.user = user;
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
}
