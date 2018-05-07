package se.javagroup.projecttask.repository.data;

import javax.persistence.*;
import java.util.Collection;

@Entity
public class Team {
    @Id
    @GeneratedValue
    private Long id;
    private String name;
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long teamNumber;
    private boolean status;
    @OneToMany(mappedBy = "team")
    private Collection<User> users;

    protected Team(){}

}
