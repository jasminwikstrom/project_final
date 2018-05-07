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
}
