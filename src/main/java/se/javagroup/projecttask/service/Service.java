package se.javagroup.projecttask.service;

import org.springframework.stereotype.Component;
import se.javagroup.projecttask.repository.IssueRepository;
import se.javagroup.projecttask.repository.TeamRepository;
import se.javagroup.projecttask.repository.UserRepository;
import se.javagroup.projecttask.repository.WorkItemRepository;
import se.javagroup.projecttask.repository.data.*;
import se.javagroup.projecttask.resource.dto.DtoWorkItem;
import se.javagroup.projecttask.service.exception.BadInputException;
import se.javagroup.projecttask.service.exception.WorkItemNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import java.util.*;
import java.util.stream.Collectors;

@Component
public final class Service {

    private final IssueRepository issueRepository;
    private final TeamRepository teamRepository;
    private final UserRepository userRepository;
    private final WorkItemRepository workItemRepository;
    private static final AtomicLong usernumbers = new AtomicLong(100);

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
                return workItemRepository.save(new WorkItem(workItem.getId(), workItemNew.getDescription(), workItemNew.getWorkItemStatus(),
                        userOptional.get()));
            }
            else if(userOptional.isPresent() && (workItemNew == null || "".equals(workItemNew))) {
                return workItemRepository.save(new WorkItem(workItem.getId(), workItem.getDescription(), workItem.getWorkItemStatus(),
                        userOptional.get()));

            }else if(workItemNew != null && !"".equals(workItemNew)){
                return workItemRepository.save(new WorkItem(workItem.getId(), workItemNew.getDescription(), workItemNew.getWorkItemStatus()));

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

        if (user.getUsername() == null) {
            throw new BadInputException("Username can not be null");
        }

        if (validateUsernameLength(user.getUsername()).equals("valid"));

        Long usernumber = randomizedUserNumber();
        boolean numberexists = checkUserNumber(usernumber);
        if (numberexists == true) {
            usernumber = randomizedUserNumber();
            numberexists = checkUserNumber(usernumber);
        }
        if (numberexists == false) {
            user.setUserNumber(usernumber);
        }
        if(user.getTeam() != null){
            if (teamIsFull(user.getTeam().getId()) == true) {
                throw new BadInputException("This team is full. Choose another team.");
            }
        }
        return userRepository.save(user);
    }


    public Optional<User> getUser(String id) {
        return userRepository.findById(Long.valueOf(id));
    }

    public boolean deleteUser(Long userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if(userOptional.isPresent()){
            User user = userOptional.get();
            for(WorkItem w : user.getWorkitems()){
                workItemRepository.save(new WorkItem(w.getId(), w.getDescription(), w.getWorkItemStatus(), null));
            }
            userRepository.delete(userOptional.get());
            return true;
        }
        return false;
    }


    public User updateUser(String id, User user) {


        return userRepository.findById(Long.valueOf(id))
                .map(u -> {
                    u.setFirstName(user.getFirstName());
                    //return userRepository.save(u); LÄGG IN TEAM HÄR?
                    return userRepository.save(u);
                }).orElseThrow(() -> new BadInputException("User with id " + id + " was not found"));
    }

    public List<User> getResult(String firstName, String lastName, String username, String teamname) {

        List<User> users = userRepository.findAll();

        if (firstName == null && lastName == null && username == null && teamname == null) {
            return users;
        }

        if(firstName != null){
            users = users.stream().filter(u -> u.getFirstName().toString().equalsIgnoreCase(firstName)).collect(Collectors.toList());
        }

        if(lastName != null){
            users = users.stream().filter(u -> u.getLastName().toString().equalsIgnoreCase(lastName)).collect(Collectors.toList());
        }

        if(username != null){
            users = users.stream().filter(u -> u.getUsername().toString().equalsIgnoreCase(username)).collect(Collectors.toList());
        }

        if(teamname != null){
            users = users.stream().filter(u -> u.getTeam().toString().equalsIgnoreCase(teamname)).collect(Collectors.toList());
        }
        return users;
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


    public List<WorkItem> getAllWorkItemsForTeam(Long teamID){
        Optional<Team> teamOptional = teamRepository.findById(teamID);
        List<WorkItem> workItems = new ArrayList<>();
        if(teamOptional.isPresent()){
            for(User u : teamOptional.get().getUsers()){
                for(WorkItem w : u.getWorkitems()){
                    workItems.add(w);
                }
            }
            return workItems;
        }
        throw new BadInputException("Team not found");
    }

    public User addUserToTeam(Long teamID, Long userID){                //Av Joel (inte hållbar lösning kanske?)
        Optional<Team> teamOptional = teamRepository.findById(teamID);
        Optional<User> userOptional = userRepository.findById(userID);
        if(!teamOptional.isPresent() || !userOptional.isPresent()){
            throw new BadInputException("Team or user doesn't exist");
        }
        User user = userOptional.get();
        return userRepository.save(new User(user.getId(), user.getFirstName(), user.getLastName(), user.getUsername(),
                                user.getUserNumber(), user.isStatus(), teamOptional.get()));
    }

    public Team getTeam(Long teamID){
        Optional<Team> teamOptional = teamRepository.findById(teamID);
        if(teamOptional.isPresent()){
            return teamOptional.get();
        }
        throw new BadInputException("Team not found");
    }

    public boolean deleteTeam(Long teamID){
        Optional<Team> teamOptional = teamRepository.findById(teamID);
        if(teamOptional.isPresent()){
            Team team = teamOptional.get();
            for(User u : team.getUsers()){
                userRepository.save(new User(u.getId(), u.getFirstName(), u.getLastName(), u.getUsername(), u.getUserNumber(),
                                    u.isStatus(), null));
            }
            teamRepository.delete(teamOptional.get());
            return true;
        }
        return false;
    }

    public String validateUsernameLength(String username) {
        if (!(username.length() < 10))
            return "valid";
        else
            throw new BadInputException("username must be 10 characters or more");

    }


    private Long randomizedUserNumber() {
        Random random = new Random();
        int randomI = random.nextInt(1000);
        Long randomII = Long.valueOf(randomI);
        return randomII;
    }

    private boolean checkUserNumber(Long usernumber) {  // använd autogenerateUserNumber tillsammans med detta
        List<User> users = userRepository.findAll();
        List<Long> numbers = new ArrayList<>();
        boolean exists = true;

        if(users == null){
            return false;
        }

        for (int i = 0; i < users.size(); i++) {
            Long number = users.get(i).getUserNumber();
            numbers.add(number);
        }
        for (int i = 0; i < numbers.size(); i++) {
            if (usernumber == numbers.get(i)) {
                exists = true;
            }
            else
                exists = false;

        }return  exists;
    }

    private boolean teamIsFull(Long teamId) {
        List<User> users = userRepository.findAll();
        Team team = teamRepository.getOne(teamId);
        int teammembers = 0;
        boolean full = true;

        if(teamId == null){
            return false;
        }
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getTeam().getId() == (team.getId())) {
                teammembers++;
            } else if (users.get(i).getTeam().getId() != (team.getId())) {
                teammembers = 0;
            }
        }
        if (teammembers < 10) {
            full = false;
        } else if (teammembers == 10) {
            full = true;
        }
        return full;
    }

    public Collection<WorkItem> getAllWorkItemsForUser(Optional<User> user) {
        return workItemRepository.findWorkItemsByUserId(user.get().getId());

    }


    public Optional<WorkItem> deleteWorkItem(Long id) {
        return  workItemRepository.findById(id).map( w -> {w.setUser(null); workItemRepository.save(w); workItemRepository.delete(w); return w;});
    }


}


