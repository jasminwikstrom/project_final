package se.javagroup.projecttask.service;

import org.springframework.stereotype.Component;
import se.javagroup.projecttask.repository.IssueRepository;
import se.javagroup.projecttask.repository.TeamRepository;
import se.javagroup.projecttask.repository.UserRepository;
import se.javagroup.projecttask.repository.WorkItemRepository;
import se.javagroup.projecttask.repository.data.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
        if(workItem.getWorkItemStatus() == null){
            return workItemRepository.save(new WorkItem(workItem.getDescription(), WorkItemStatus.UNSTARTED));
        }
        return workItemRepository.save(new WorkItem(workItem.getDescription(), workItem.getWorkItemStatus()));
    }


    public Team createTeam(Team team){
        return teamRepository.save(new Team(team.getName(), team.isStatus(), team.getTeamNumber()));
    }
    public Iterable<Team> getAllTeams(){
        return teamRepository.findAll();
    }

    public Team updateTeam(String id, Team team){
        return teamRepository.findById(Long.valueOf(id))
                .map(t -> {
            t.setName(team.getName());
            t.setStatus(team.isStatus());
            t.setTeamNumber(team.getTeamNumber());
            return teamRepository.save(t);
        }).orElseThrow(() -> new BadInputException("Team with id " + id + " was not found"));
    }

    public Optional<WorkItem> getWorkItem(Long id) {
        return workItemRepository.findById(id);
    }


    public Optional<Issue> getIssue(Long id){
        return issueRepository.findById(id);
    }

    public Issue createIssue(Issue issue){
        return issueRepository.save(new Issue(issue.getDescription(), issue.getWorkItem()));
    }

    public Issue updateIssue(Long id, Issue issue){
       return issueRepository.findById(id)
               .map(i -> {
                   i.setDescription(issue.getDescription());
                   i.setWorkItem(issue.getWorkItem());
                   return issueRepository.save(i);
               }).orElseThrow(() -> new BadInputException("Issue with id " + id + " was not found"));
    }


    public void deleteIssue(Issue issue) {
        issueRepository.deleteById(issue.getId());
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


    public User updateUser(String id, User user) {


        return userRepository.findById(Long.valueOf(id))
                .map(u -> {
                    u.setFirstName(user.getFirstName());
                    return userRepository.save(u);
                }).orElseThrow(() -> new BadInputException("User with id " + id + " was not found"));
    }


    public List<User> getResult(String firstName, String lastName) {
        return userRepository.findAllByQuery(firstName, lastName);


    }

    public List<WorkItem> getAllWorkItems(String status, boolean issue, String text) {
        List<WorkItem> workItems = workItemRepository.findAll();
        if(status == null && !issue && text == null) {//returerar all workItems
            return workItems;
        }
        if(issue){ //sorterar listan att endast innehålla workitems med issues
            workItems = workItems.stream().filter(w -> w.getIssue() != null).collect(Collectors.toList());
        }
        if(status!= null) { // filtrerar listan efter inmatad status
            workItems = workItems.stream().filter(w -> w.getWorkItemStatus().toString().equalsIgnoreCase(status)).collect(Collectors.toList());
        }
        if(text != null){ //sorterar listan efter innehhåll av text
           workItems = workItems.stream().filter(w -> w.getDescription().contains(text)).collect(Collectors.toList());
        }
        return workItems;
    }

}


