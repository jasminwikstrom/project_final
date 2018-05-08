package se.javagroup.projecttask.service;

import org.springframework.stereotype.Component;
import se.javagroup.projecttask.repository.IssueRepository;
import se.javagroup.projecttask.repository.TeamRepository;
import se.javagroup.projecttask.repository.UserRepository;
import se.javagroup.projecttask.repository.WorkItemRepository;
import se.javagroup.projecttask.repository.data.WorkItem;
import se.javagroup.projecttask.repository.data.Issue;
import se.javagroup.projecttask.repository.data.Team;
import se.javagroup.projecttask.repository.data.User;
import se.javagroup.projecttask.repository.data.WorkItem;

import java.util.List;
import java.util.Optional;

@org.springframework.stereotype.Service
@Component
public final class Service {

    private final IssueRepository issueRepository;
    private final TeamRepository teamRepository;
    private final UserRepository userRepository;
    private final WorkItemRepository workItemRepository;

    public Service(IssueRepository issueRepository, TeamRepository teamRepository, UserRepository userRepository, WorkItemRepository workItemRepository) {
        this.issueRepository = issueRepository;
        this.teamRepository = teamRepository;
        this.userRepository = userRepository;
        this.workItemRepository = workItemRepository;
    }


    public WorkItem createWorkItem(WorkItem workItem) {
        return workItemRepository.save(new WorkItem(workItem.getDescription(), workItem.getWorkItemStatus()));
    }

    public Team addTeam(Team team){

    public Team addTeam(Team team) {
        return teamRepository.save(team);
    }
    public List<Team> getAllTeams(){

    public List<Team> getAllTeams() {
        return teamRepository.getAllTeams();
    }
    public Team updateTeam (Team team){

    public Team updateTeam(Team team) {
        return teamRepository.save(team);
    }

    public Optional<WorkItem> getWorkItem(Long id){
    public Optional<WorkItem> getWorkItem(Long id) {
        return workItemRepository.findById(id);
    }

<<<<<<< HEAD
    public Optional<Issue> getIssue(Long id){
        return issueRepository.findById(id);
    }

    public Issue createIssue(Issue issue){
        return issueRepository.save(new Issue(issue.getDescription(), issue.getWorkItem()));
    }

    public Issue updateIssue(String id, String description, WorkItem workItem){
       return issueRepository.findById(Long.valueOf(id))
               .map(i -> {
                   i.setDescription(String.valueOf(description));
                   i.setWorkItem(workItem);
                   return issueRepository.save(i);
               }).orElseThrow(() -> new BadInputException("Issue with id " + id + " was not found"));
    }


    public void deleteIssue(Issue issue) {
        issueRepository.deleteById(issue.getId());
=======
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


    public User getUser(String id) {
        return userRepository.findById(Long.valueOf(id))
                .map(User::new)
                .orElseThrow(() -> new javax.ws.rs.NotFoundException("User with id " + id + " not found"));
    }

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

>>>>>>> master
    }
}


