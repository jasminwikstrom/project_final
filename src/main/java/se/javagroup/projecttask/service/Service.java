package se.javagroup.projecttask.service;

import org.springframework.stereotype.Component;
import se.javagroup.projecttask.repository.IssueRepository;
import se.javagroup.projecttask.repository.TeamRepository;
import se.javagroup.projecttask.repository.UserRepository;
import se.javagroup.projecttask.repository.WorkItemRepository;
import se.javagroup.projecttask.repository.data.*;
import se.javagroup.projecttask.resource.dto.WorkItemDto;
import se.javagroup.projecttask.service.exception.BadInputException;
import se.javagroup.projecttask.service.exception.TeamNotFoundException;
import se.javagroup.projecttask.service.exception.WorkItemNotFoundException;

import java.util.*;
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

    public User createUser(User user) {
        validateName(user);
        validateUsernameLength(user.getUsername());
        Long usernumber = randomizedUserNumber();
        boolean numberexists = checkUserNumber(usernumber);
        if (numberexists == true) {
            usernumber = randomizedUserNumber();
            numberexists = checkUserNumber(usernumber);
            user.setUserNumber(usernumber);
        }
        if (numberexists == false) {
            user.setUserNumber(usernumber);
        }
        return userRepository.save(user);
    }

    public List<User> getAllUsers(String firstName, String lastName, String userName, String teamName, String userNumber) {
        return userRepository.findAllByQuery(firstName, lastName, userName, teamName, userNumber);
    }

    public User updateUser(Long userNumber, User user) {
        User foundUser = userRepository.findByUserNumber(userNumber).get();

        foundUser.setFirstName(user.getFirstName());
        foundUser.setLastName(user.getLastName());
        foundUser.setUsername(user.getUsername());
        foundUser.setStatus(user.isStatus());
            if(!foundUser.isStatus()){
                Collection<WorkItem> foundWorkItems = workItemRepository.findWorkItemsByUserId(foundUser.getId());
                foundWorkItems.forEach(w -> workItemRepository.save(new WorkItem(w.getId(), w.getDescription(), WorkItemStatus.UNSTARTED)));
            }
        return userRepository.save(foundUser);
    }

    public Optional<User> getUserByUserNumber(Long userNumber) {
        return userRepository.findByUserNumber(userNumber);
    }

    public boolean deleteUser(Long userNumber) {
        Optional<User> userOptional = userRepository.findByUserNumber(userNumber);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            for (WorkItem w : user.getWorkitems()) {
                workItemRepository.save(new WorkItem(w.getId(), w.getDescription(), w.getWorkItemStatus(), null));
            }
            userRepository.delete(userOptional.get());
            return true;
        }
        return false;
    }

    public WorkItem createWorkItem(WorkItemDto workItem) {
        Optional<String> status = Optional.ofNullable(workItem.getWorkItemStatus());
        if (!status.isPresent()){
            return workItemRepository.save(new WorkItem(null, workItem.getDescription(), WorkItemStatus.UNSTARTED));
        }
        return status.filter(s-> s.toUpperCase().equalsIgnoreCase("UNSTARTED")
                || s.toUpperCase().equalsIgnoreCase("STARTED")
                || s.toUpperCase().equalsIgnoreCase("DONE"))
                .map(m -> workItemRepository.save(new WorkItem(null, workItem.getDescription(), WorkItemStatus.valueOf(m.toUpperCase()))))
                .orElseThrow(() -> new BadInputException(workItem.getWorkItemStatus() + " - Wrong status type"));
    }

    public List<WorkItem> getAllWorkItems(String status, boolean issue, String text) {
        List<WorkItem> workItems = workItemRepository.findAll();
        if (status == null && !issue && text == null) {
            return workItems;
        }
        if (issue) {
            workItems = workItems.stream().filter(w -> w.getIssue() != null).collect(Collectors.toList());
        }
        if (status != null) {

            workItems = workItems.stream().filter(w -> w.getWorkItemStatus().toString().equalsIgnoreCase(status)).collect(Collectors.toList());
        }
        if (text != null) {
            workItems = workItems.stream().filter(w -> w.getDescription().contains(text)).collect(Collectors.toList());
        }
        return workItems;
    }

    public Collection<WorkItem> getAllWorkItemsForUser(Optional<User> user) {
        return workItemRepository.findWorkItemsByUserId(user.get().getId());
    }

    public List<WorkItem> getAllWorkItemsForTeam(Long teamId) {
        Optional<Team> teamOptional = teamRepository.findById(teamId);
        List<WorkItem> workItems = new ArrayList<>();
        if (teamOptional.isPresent()) {
            for (User u : teamOptional.get().getUsers()) {
                for (WorkItem w : u.getWorkitems()) {
                    workItems.add(w);
                }
            }
            return workItems;
        }
        throw new TeamNotFoundException(String.format("Team with id %s was not found", teamId));
    }

    public Optional<WorkItem> getWorkItem(Long workItemId) {
        Optional<WorkItem> workItem = workItemRepository.findById(workItemId);
        if(workItem.isPresent()){
            return workItem;
        }
        throw new WorkItemNotFoundException(String.format("WorkItem with id %s was not found", workItemId));
    }

    public WorkItem updateWorkItem(Long workItemId, WorkItem workItemNew, Long userNumber) {
        WorkItem workItem = validateWorkItem(workItemId);
        if (userExists(userNumber)) {
            User user = userRepository.findByUserNumber(userNumber).get();
            if (maxWorkItemCount(user)) {
                throw new BadInputException("Maximum amount of workitems reached for user");
            }
            if (workItemNew == null) {
                return workItemRepository.save(new WorkItem(workItem.getId(), workItem.getDescription(), workItem.getWorkItemStatus(),
                        user));
            }
            return workItemRepository.save(new WorkItem(workItem.getId(), workItemNew.getDescription(), workItemNew.getWorkItemStatus(),
                    user));
        }
        if (workItemNew == null) {
            return workItem;
        }
        return workItemRepository.save(new WorkItem(workItem.getId(), workItemNew.getDescription(), workItemNew.getWorkItemStatus(),
                workItem.getUser()));
    }

    public Optional<WorkItem> deleteWorkItem(Long workItemId) {
        return workItemRepository.findById(workItemId).map(w -> {
            w.setUser(null);
            workItemRepository.save(w);
            workItemRepository.delete(w);
            return Optional.ofNullable(w);
        }).orElseThrow(() -> new WorkItemNotFoundException(String.format("WorkItem with id %s was not found", workItemId)));
    }

    public Team createTeam (Team team){
        return teamRepository.save(team);
    }

    public Iterable<Team> getAllTeams () {
        return teamRepository.findAll();
    }

    public Optional<Team> getTeam (Long teamId){
        return teamRepository.findById(teamId);
    }

    public Team updateTeam (Long teamId, Team team){
        return teamRepository.findById(teamId)
                .map(t -> teamRepository.save(team)).orElseThrow(() ->
                        new TeamNotFoundException(String.format("Team with id %s was not found", teamId)));
    }

    public User addUserToTeam (Long teamId, Long userNumber){
        Optional<Team> teamOptional = teamRepository.findById(teamId);
        Optional<User> userOptional = userRepository.findByUserNumber(userNumber);
        if (!teamOptional.isPresent() || !userOptional.isPresent()) {
            throw new BadInputException("Team or user doesn't exist");
        }
        if (teamOptional.isPresent()) {
            if (teamIsFullTest(teamOptional.get())) {
                throw new BadInputException("Team is full");
            }
        }
        User user = userOptional.get();
        return userRepository.save(new User(user.getId(), user.getFirstName(), user.getLastName(), user.getUsername(),
                user.getUserNumber(), user.isStatus(), teamOptional.get()));
    }

    public boolean deleteTeam (Long teamId){
        Optional<Team> teamOptional = teamRepository.findById(teamId);
        Collection<User> users = teamOptional.map(t -> t.getUsers()).orElseThrow(() ->
                new TeamNotFoundException(String.format("Team with id %s was not found", teamId)));
        users.stream().forEach(u -> userRepository.save(new User(u.getId(), u.getFirstName(), u.getLastName(),
                u.getUsername(), u.getUserNumber(), u.isStatus(),
                null)));
        teamRepository.delete(teamOptional.get());
        return true;
    }

    public Optional<Issue> createIssue(Issue issue, Long workItemId) {
        Optional<WorkItem> foundWorkItem = workItemRepository.findById(workItemId);
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

    public Optional<Issue> getIssue(Long issueId) {
        return issueRepository.findById(issueId);
    }

    public Issue updateIssue(Long issueId, Issue issue) {
        return issueRepository.findById(issueId)
                .map(i -> issueRepository.save(issue)).orElseThrow(() ->
                        new BadInputException(String.format("Issue with id %s was not found", issueId)));
    }

    public void deleteIssue(Issue issue) {
        issueRepository.deleteById(issue.getId());
    }

    private String validateUsernameLength(String userName) {
        if (!(userName.length() < 10))
            return "valid";
        else
            throw new BadInputException("username must be 10 characters or more");
    }

    private void validateName(User user){
        if (user.getFirstName() == null) {
            throw new BadInputException("Firstname can not be null");
        }
        if (user.getLastName() == null) {
            throw new BadInputException("Lastname can not be null");
        }
        if (user.getUsername() == null) {
            throw new BadInputException("Username can not be null");
        }
    }

    private Long randomizedUserNumber() {
        Random random = new Random();
        int randomI = random.nextInt(1000);
        Long randomII = Long.valueOf(randomI);
        return randomII;
    }

    private boolean checkUserNumber(Long userNumber) {
        for(User u : userRepository.findAll()){
            if(userNumber == u.getUserNumber()){
                return true;
            }
        }
        return false;
    }

    private boolean teamIsFullTest (Team team){
        if (team.getUsers() != null) {
            if (team.getUsers().size() >= 10) {
                return true;
            }
        }
        return false;
    }

    private boolean maxWorkItemCount(User user) {
        if (user.getWorkitems().size() >= 5) {
            return true;
        }
        return false;
    }

    private WorkItem validateWorkItem(Long workItemId) {
        Optional<WorkItem> workItem = workItemRepository.findById(workItemId);
        if (!workItem.isPresent()) {
            throw new BadInputException("WorkItem not found");
        }
        return workItem.get();
    }

    private boolean userExists(Long userNumber) {
        if (userRepository.findByUserNumber(userNumber).isPresent()) {
            return true;
        }
        return false;
    }
}