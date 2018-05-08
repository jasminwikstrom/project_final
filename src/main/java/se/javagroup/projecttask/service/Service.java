package se.javagroup.projecttask.service;

import se.javagroup.projecttask.repository.IssueRepository;
import se.javagroup.projecttask.repository.TeamRepository;
import se.javagroup.projecttask.repository.UserRepository;
import se.javagroup.projecttask.repository.WorkItemRepository;
import se.javagroup.projecttask.repository.data.Issue;
import se.javagroup.projecttask.repository.data.Team;
import se.javagroup.projecttask.repository.data.User;
import se.javagroup.projecttask.repository.data.WorkItem;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

import static java.util.stream.Collectors.toList;

@org.springframework.stereotype.Service
public final class Service {

    private final IssueRepository issueRepository;
    private final TeamRepository teamRepository;
    private final UserRepository userRepository;
    private final WorkItemRepository workItemRepository;
    private static final AtomicLong ids = new AtomicLong(1000);

    public Service(IssueRepository issueRepository, TeamRepository teamRepository, UserRepository userRepository, WorkItemRepository workItemRepository) {
        this.issueRepository = issueRepository;
        this.teamRepository = teamRepository;
        this.userRepository = userRepository;
        this.workItemRepository = workItemRepository;
    }

    public WorkItem createWorkItem(WorkItem workItem) {
        return workItemRepository.save(new WorkItem(workItem.getDescription(), workItem.getWorkItemStatus()));
    }


    public Team addTeam(Team team) {
        return teamRepository.save(team);
    }

    public List<Team> getAllTeams() {
        return teamRepository.getAllTeams();
    }

    public Team updateTeam(Team team) {
        return teamRepository.save(team);
    }

    public Optional<WorkItem> getWorkItem(Long id) {
        return workItemRepository.findById(id);
    }

    public Issue createIssue(Issue issue) {
        return issueRepository.save(new Issue(issue.getDescription(), issue.getWorkItem()));
    }


    public User saveUser(User user) {

        if (user.getFirstName() == null) {
            throw new BadInputException("Firstname can not be null");

        }

        if (user.getLastName() == null) {
            throw new BadInputException("Lastname can not be null");
        }


        return userRepository.save(user);
    }


   /* public User getUser(String id) {
        return userRepository.findById(Long.valueOf(id))
                .map(User::new)
                .orElseThrow(() -> new javax.ws.rs.NotFoundException("User with id " + id + " not found"));
    }*/

    public void deleteUser(String userId) {
        userRepository.findById(Long.valueOf(userId)).ifPresent(userRepository::delete);
    }


    public User updateUser(String id, String firstName) {


        return userRepository.findById(Long.valueOf(id))
                .map(user -> {
                    user.setFirstName(String.valueOf(firstName));
                    return userRepository.save(user);
                }).orElseThrow(() -> new BadInputException("User with id " + id + " was not found"));
    }


    public List<User> getResult(String firstName, String lastName) {
        return userRepository.findAllByQuery(firstName, lastName);

    }


    public User createUser(User user) {

        return userRepository.add(new User(ids.incrementAndGet(),
                user.getFirstName(), user.getLastName(), user.getUsername(), user.getUserNumber(), user.isStatus()));
    }

    public Optional<User> getUser(Long id) {
        return userRepository.get(id);
    }

    public List<User> getAllUsers() {
        return userRepository.getAll().collect(toList());
    }

    public User updateUser(User user) {

        return userRepository.update(user);
    }

    public Optional<User> deleteCustomer(Long id) {
        return userRepository.delete(id);
    }


}
