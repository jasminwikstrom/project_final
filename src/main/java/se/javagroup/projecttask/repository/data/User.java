package se.javagroup.projecttask.repository.data;

import javax.persistence.*;
@Entity
public class User {
    @Id
    @GeneratedValue
    private Long id;
    @Column(nullable = false)
    private String firstName;
    @Column(nullable = false)
    private String lastName;
    private String username;
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long userNumber;
    private boolean status;
    @ManyToOne
    private Team team;
    //@JsonManagedReference behövs ev.

    protected User(){}

    public User(Long id, String firstName, String lastName, String username, Long userNumber, boolean status) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.userNumber = userNumber;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getUsername() {
        return username;
    }

    public Long getUserNumber() {
        return userNumber;
    }

    public boolean isStatus() {
        return status;
    }

    public Team getTeam() {
        return team;
    }
}
