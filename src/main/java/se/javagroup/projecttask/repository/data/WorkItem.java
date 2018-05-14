package se.javagroup.projecttask.repository.data;

import com.fasterxml.jackson.annotation.JsonBackReference;
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
    @JsonBackReference
    private User user;
    //@XmlTransient
    @OneToOne(mappedBy = "workItem", fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
    @JsonManagedReference
    private Issue issue;

    protected WorkItem() {
    }

    public WorkItem(String description, WorkItemStatus workItemStatus) {
        this.description = description;
        this.workItemStatus = workItemStatus;
    }

    public WorkItem(Long id, String description, WorkItemStatus workItemStatus) {
        this.id = id;
        this.description = description;
        this.workItemStatus = workItemStatus;
    }

    public WorkItem(Long id, String description, WorkItemStatus workItemStatus, User user) {
        this.id = id;
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

    public void setIssue(Issue issue) {
        this.issue = issue;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
