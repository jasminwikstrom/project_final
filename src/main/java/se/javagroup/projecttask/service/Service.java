package se.javagroup.projecttask.service;

import org.hashids.Hashids;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import se.javagroup.projecttask.repository.IssueRepository;
import se.javagroup.projecttask.repository.TeamRepository;
import se.javagroup.projecttask.repository.UserRepository;
import se.javagroup.projecttask.repository.WorkItemRepository;
import se.javagroup.projecttask.repository.data.*;
import se.javagroup.projecttask.service.exception.BadInputException;
import se.javagroup.projecttask.service.exception.WorkItemNotFoundException;
import se.javagroup.projecttask.resource.dto.DtoWorkItem;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
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


    public WorkItem createWorkItem(DtoWorkItem workItem) {
        if(workItem.getWorkItemStatus() == null){
            return workItemRepository.save(new WorkItem(null, workItem.getDescription(), WorkItemStatus.UNSTARTED));

        }
        if(workItem.getWorkItemStatus().toUpperCase().equalsIgnoreCase("UNSTARTED")
                || workItem.getWorkItemStatus().toUpperCase().equalsIgnoreCase("STARTED")
                || workItem.getWorkItemStatus().toUpperCase().equalsIgnoreCase("DONE")) {
            return workItemRepository.save(new WorkItem(null, workItem.getDescription(), WorkItemStatus.valueOf(workItem.getWorkItemStatus().toUpperCase())));
        }
        throw new BadInputException(workItem.getWorkItemStatus() + " - Wrong status type");
    }

    public WorkItem updateWorkItem(Long workItemId, WorkItem workItemNew, Long userId){
        Optional<WorkItem> workItemOptional = workItemRepository.findById(workItemId);
        Optional<User> userOptional = userRepository.findById(userId);
        if(workItemOptional.isPresent()){
            WorkItem workItem = workItemOptional.get();
            if(userOptional.isPresent() && !(workItemNew == null || "".equals(workItemNew))){
                workItemRepository.save(new WorkItem(workItem.getId(), workItemNew.getDescription(), workItemNew.getWorkItemStatus(),
                        userOptional.get()));
            }
            else if(userOptional.isPresent() && (workItemNew == null || "".equals(workItemNew))) {
                workItemRepository.save(new WorkItem(workItem.getId(), workItem.getDescription(), workItem.getWorkItemStatus(),
                        userOptional.get()));
            }
            else {
                workItemRepository.save(new WorkItem(workItem.getId(), workItemNew.getDescription(), workItemNew.getWorkItemStatus()));
            }
            return workItem;
        }
        throw new WorkItemNotFoundException(String.format("WorkItem %s not found", workItemId));
    }


    public Team createTeam(Team team) {
        return teamRepository.save(new Team(team.getName(), team.isStatus(), team.getTeamNumber()));
    }

    public Iterable<Team> getAllTeams() {
        return teamRepository.findAll();
    }

    public Team updateTeam(String id, Team team) {
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

    public Optional<Issue> getIssue(Long id) {

        return issueRepository.findById(id);
    }

    public Optional<Issue> createIssue(Issue issue, Long workItemID) {

        Optional<WorkItem> foundWorkItem = workItemRepository.findById(workItemID);

        if (foundWorkItem.isPresent()) {
            WorkItem oldWorkItem = foundWorkItem.get();
            if (oldWorkItem.getWorkItemStatus().toString().equals("DONE")) {
                Optional<Issue> newIssue = Optional.of(issueRepository.save(new Issue(issue.getDescription(), issue.getWorkItem())));
                workItemRepository.save(new WorkItem(oldWorkItem.getId(), oldWorkItem.getDescription(), WorkItemStatus.UNSTARTED));

                return newIssue;

            }
        }
        throw new BadInputException("You can't create an issue if the workitem is unstarted or just started");
    }

    public Issue updateIssue(Long id, Issue issue) {
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


        // skulle kunna validera username här också

        if (user.getUsername() == null) {
            throw new BadInputException("Username can not be null");
        }


        //return userRepository.save(user);
        //NYTT från cla
        return userRepository.save(new User(user.getId(), user.getFirstName(),
                user.getLastName(), user.getUsername(), user.getUserNumber(), user.isStatus(), user.getTeam()));
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


    public List<User> getResult(String firstName, String lastName, String username, String teamname) {
        return userRepository.findAllByQuery(firstName, lastName, username, teamname);


    }


    public List<WorkItem> getAllWorkItems(String status, boolean issue, String text) {
        List<WorkItem> workItems = workItemRepository.findAll();
        if (status == null && !issue && text == null) {//returerar all workItems
            return workItems;
        }
        if (issue) { //sorterar listan att endast innehålla workitems med issues
            workItems = workItems.stream().filter(w -> w.getIssue() != null).collect(Collectors.toList());
        }
        if (status != null) { // filtrerar listan efter inmatad status

            workItems = workItems.stream().filter(w -> w.getWorkItemStatus().toString().equalsIgnoreCase(status)).collect(Collectors.toList());
        }
        if (text != null) { //sorterar listan efter innehhåll av text
            workItems = workItems.stream().filter(w -> w.getDescription().contains(text)).collect(Collectors.toList());
        }
        return workItems;
    }

    public String validateUsernameLength(String username) {
        if (!(username.length() < 10))
            return "valid";
        else
            throw new BadInputException("username must be 10 characters or more");

    }

}


