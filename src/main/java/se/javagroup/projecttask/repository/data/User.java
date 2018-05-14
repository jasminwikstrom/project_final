package se.javagroup.projecttask.repository.data;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import javax.persistence.*;
import java.util.Collection;

@Entity
public class User {
    @Id
    @GeneratedValue
    private Long id;
    @Column(nullable = false)
    private String firstName;
    @Column(nullable = false)
    private String lastName;
    @Column(nullable = false)
    private String username;
    // @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false)
    private Long userNumber;
    private boolean status;
    @ManyToOne
    @JsonBackReference
    private Team team;

    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER)
    //@OneToMany(mappedBy = "user")
    @JsonManagedReference
    private Collection<WorkItem> workitems;
    //@JsonManagedReference behövs ev.

    public User() {
    }

    public User(Long id, String firstName, String lastName, String username, Long userNumber, boolean status, Team team) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.userNumber = userNumber;
        this.status = status;
        this.team = team;//NYTT från cla

    }

    public User(String firstName, String lastName, String username) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Long getUserNumber() {
        return userNumber;
    }

    public void setUserNumber(Long userNumber) {
        this.userNumber = userNumber;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public Collection<WorkItem> getWorkitems() {
        return workitems;
    }
}
