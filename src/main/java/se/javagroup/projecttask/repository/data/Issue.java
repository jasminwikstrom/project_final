package se.javagroup.projecttask.repository.data;

import javax.persistence.*;

@Entity
public class Issue {
    @Id
    @GeneratedValue
    private Long id;
    @Column(nullable = false)
    private String description;
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long issueNumber;
    @OneToOne
    private WorkItem workItem;

    protected Issue(){}

    public Issue(String description, WorkItem workItem) {
        this.description = description;
        this.workItem = workItem;
    }

    public Long getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public Long getIssueNumber() {
        return issueNumber;
    }

    public WorkItem getWorkItem() {
        return workItem;
    }

    public void setWorkItem(WorkItem workItem) {
        this.workItem = workItem;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setIssueNumber(Long issueNumber) {
        this.issueNumber = issueNumber;
    }
}
