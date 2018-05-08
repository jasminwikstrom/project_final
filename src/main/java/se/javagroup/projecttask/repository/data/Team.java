package se.javagroup.projecttask.repository.data;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlTransient;
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
    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL)
    @XmlTransient//För att de ej ska bli proxy
    private Collection<User> users;

    protected Team() {
    }

    public Team(String name, boolean status, Long teamNumber) {
        this.name = name;
        this.status = status;
        this.teamNumber = teamNumber;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getTeamNumber() {
        return teamNumber;
    }

    public void setTeamNumber(Long teamNumber) {
        this.teamNumber = teamNumber;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public Collection<User> getUsers() {
        return users;
    }
}
