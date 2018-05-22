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
    private boolean status;
    @OneToMany(mappedBy = "team", fetch = FetchType.EAGER)
    @JsonManagedReference
    private Collection<User> users;

    protected Team() {
    }

    public Team(String name, boolean status) {
        this.name = name;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public Collection<User> getUsers() {
        return users;
    }
}
