package se.javagroup.projecttask.repository.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import javax.persistence.*;
@Entity
public class WorkItem {
    @Id
    @GeneratedValue
    private Long id;
    @Column(nullable = false)
    private String description;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WorkItemStatus workItemStatus;
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long workItemNumber;
    @ManyToOne()
    private User user;
    @OneToOne(mappedBy = "workItem", fetch = FetchType.EAGER)
    @JsonManagedReference
    private Issue issue;

    protected WorkItem(){}

    public WorkItem(String description, WorkItemStatus workItemStatus) {
        this.description = description;
        this.workItemStatus = workItemStatus;
    }

    public Long getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public WorkItemStatus getWorkItemStatus() {
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
}
