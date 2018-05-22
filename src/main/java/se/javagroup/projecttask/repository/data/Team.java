package se.javagroup.projecttask.repository.data;

import com.fasterxml.jackson.annotation.JsonManagedReference;

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
    @OneToMany(mappedBy = "team", fetch = FetchType.EAGER)
    @JsonManagedReference
    private Collection<User> users;

    protected Team() {}

    public Team(String name, boolean status, Long teamNumber) {
        this.name = name;
        this.status = status;
        this.teamNumber = teamNumber;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Long getTeamNumber() {
        return teamNumber;
    }

    public boolean isStatus() {
        return status;
    }

    public Collection<User> getUsers() {
        return users;
    }
}
