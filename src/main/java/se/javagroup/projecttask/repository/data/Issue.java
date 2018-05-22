package se.javagroup.projecttask.repository.data;

import com.fasterxml.jackson.annotation.JsonBackReference;

import javax.persistence.*;

@Entity
public class Issue {
    @Id
    @GeneratedValue
    private Long id;
    @Column(nullable = false)
    private String description;
    @OneToOne()
    @JsonBackReference
    private WorkItem workItem;

    protected Issue() {
    }

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

    public WorkItem getWorkItem() {
        return workItem;
    }
}