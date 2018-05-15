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
        if (user.getFirstName() == null) {
            throw new BadInputException("Firstname can not be null");
        }
        if (user.getLastName() == null) {
            throw new BadInputException("Lastname can not be null");
        }
        if (user.getUsername() == null) {
            throw new BadInputException("Username can not be null");
        }
        if (validateUsernameLength(user.getUsername()).equals("valid")) ;
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
        if (user.getTeam() != null) {
            if (teamIsFull(user.getTeam().getId()) == true) {
                throw new BadInputException("This team is full. Choose another team.");
            }
        }
        return userRepository.save(user);
    }

    public List<User> getAllUsers(String firstName, String lastName, String username, String teamname, String userNumber) {
        return userRepository.findAllByQuery(firstName, lastName, username, teamname, userNumber);
    }

    public Optional<User> getUser(String userId) {
        return userRepository.findById(Long.valueOf(userId));
    }

    public User updateUser(String userId, User user) {
        if (teamIsFull(user.getTeam().getId()) == true) {
            throw new BadInputException("This team is full. Choose another team.");
        }
        return userRepository.findById(Long.valueOf(userId))
                .map(u -> {
                    u.setTeam(user.getTeam());
                    return userRepository.save(u);
                }).orElseThrow(() -> new BadInputException("User with id " + userId + " was not found"));
    }

    public boolean deleteUser(Long userId) {
        Optional<User> userOptional = userRepository.findById(userId);
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
        if (workItem.getWorkItemStatus() == null) {
            return workItemRepository.save(new WorkItem(null, workItem.getDescription(), WorkItemStatus.UNSTARTED));
        }
        if (workItem.getWorkItemStatus().toUpperCase().equalsIgnoreCase("UNSTARTED")
                || workItem.getWorkItemStatus().toUpperCase().equalsIgnoreCase("STARTED")
                || workItem.getWorkItemStatus().toUpperCase().equalsIgnoreCase("DONE")) {
            return workItemRepository.save(new WorkItem(null, workItem.getDescription(), WorkItemStatus.valueOf(workItem.getWorkItemStatus().toUpperCase())));
        }
        throw new BadInputException(workItem.getWorkItemStatus() + " - Wrong status type");
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
        throw new TeamNotFoundException(String.format("Team with id %s not found", teamId));
    }

    public Optional<WorkItem> getWorkItem(Long workItemId) {
        return workItemRepository.findById(workItemId);
    }

    public WorkItem updateWorkItem(Long workItemId, WorkItem workItemNew, Long userId) {
        Optional<WorkItem> workItemOptional = workItemRepository.findById(workItemId);
        Optional<User> userOptional = userRepository.findById(userId);
        if (workItemOptional.isPresent()) {
            WorkItem workItem = workItemOptional.get();
            if (userOptional.isPresent() && !(workItemNew == null || "".equals(workItemNew))) {
                return workItemRepository.save(new WorkItem(workItem.getId(), workItemNew.getDescription(), workItemNew.getWorkItemStatus(),
                        userOptional.get()));
            } else if (userOptional.isPresent() && (workItemNew == null || "".equals(workItemNew))) {
                return workItemRepository.save(new WorkItem(workItem.getId(), workItem.getDescription(), workItem.getWorkItemStatus(),
                        userOptional.get()));
            } else if (workItemNew != null && !"".equals(workItemNew)) {
                return workItemRepository.save(new WorkItem(workItem.getId(), workItemNew.getDescription(), workItemNew.getWorkItemStatus()));
            }
            return workItem;
        }
        throw new WorkItemNotFoundException(String.format("WorkItem %s not found", workItemId));
    }

    public Optional<WorkItem> deleteWorkItem(Long workItemId) {
        return workItemRepository.findById(workItemId).map(w -> {
            w.setUser(null);
            workItemRepository.save(w);
            workItemRepository.delete(w);
            return w;
        });
    }

    public Team createTeam(Team team) {
        return teamRepository.save(team);
    }

    public Iterable<Team> getAllTeams() {
        return teamRepository.findAll();
    }

    public Team getTeam(Long teamId) {
        Optional<Team> teamOptional = teamRepository.findById(teamId);
        if (teamOptional.isPresent()) {
            return teamOptional.get();
        }
        throw new TeamNotFoundException(String.format("Team with id %s not found", teamId));
    }

    public Team updateTeam(Long teamId, Team team) {
        return teamRepository.findById(teamId)
                .map(t -> {
                    t.setName(team.getName());
                    t.setStatus(team.isStatus());
                    t.setTeamNumber(team.getTeamNumber());
                    return teamRepository.save(t);
                }).orElseThrow(() -> new TeamNotFoundException(String.format("Team with id %s was not found", teamId)));
    }

    public User addUserToTeam(Long teamId, Long userId) {
        Optional<Team> teamOptional = teamRepository.findById(teamId);
        Optional<User> userOptional = userRepository.findById(userId);
        if (!teamOptional.isPresent() || !userOptional.isPresent()) {
            throw new BadInputException("Team or user doesn't exist");
        }
        User user = userOptional.get();
        return userRepository.save(new User(user.getId(), user.getFirstName(), user.getLastName(), user.getUsername(),
                user.getUserNumber(), user.isStatus(), teamOptional.get()));
    }

    public boolean deleteTeam(Long teamId) {
        Optional<Team> teamOptional = teamRepository.findById(teamId);
        if (teamOptional.isPresent()) {
            Team team = teamOptional.get();
            for (User u : team.getUsers()) {
                userRepository.save(new User(u.getId(), u.getFirstName(), u.getLastName(), u.getUsername(), u.getUserNumber(),
                        u.isStatus(), null));
            }
            teamRepository.delete(teamOptional.get());
            return true;
        }
        throw new TeamNotFoundException(String.format("Team with id %s not found", teamId));
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
                .map(i -> {
                    i.setDescription(issue.getDescription());
                    i.setWorkItem(issue.getWorkItem());
                    return issueRepository.save(i);
                }).orElseThrow(() -> new BadInputException("Issue with id " + issueId + " was not found"));
    }

    public void deleteIssue(Issue issue) {
        issueRepository.deleteById(issue.getId());
    }

    private String validateUsernameLength(String username) {
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

    private boolean checkUserNumber(Long usernumber) {
        List<User> users = userRepository.findAll();
        List<Long> numbers = new ArrayList<>();
        boolean exists = true;
        if (users == null) {
            return false;
        }
        for (int i = 0; i < users.size(); i++) {
            Long number = users.get(i).getUserNumber();
            numbers.add(number);
        }
        for (int i = 0; i < numbers.size(); i++) {
            if (usernumber == numbers.get(i)) {
                exists = true;
            } else
                exists = false;
        }
        return exists;
    }

    private boolean teamIsFull(Long teamId) {
        List<User> users = userRepository.findAll();
        Team team = teamRepository.getOne(teamId);
        int teamMembers = 0;
        boolean full = false;
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getTeam() == null) {
                users.get(i).setTeam(team);
            } else {
                if (users.get(i).getTeam().getId().equals(team.getId())) {
                    teamMembers++;
                } else if (users.get(i).getTeam().getId() != team.getId()) {
                    teamMembers = 0;
                }
            }
        }
        if (teamMembers < 10) {
            full = false;
        } else if (teamMembers == 10) {
            full = true;
        }
        return full;
    }
}