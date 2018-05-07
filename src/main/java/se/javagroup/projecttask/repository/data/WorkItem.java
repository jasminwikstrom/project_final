package se.javagroup.projecttask.repository.data;

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
    @OneToOne
    private Issue issue;

    protected WorkItem(){}
}
