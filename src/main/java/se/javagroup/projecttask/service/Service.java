package se.javagroup.projecttask.service;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import se.javagroup.projecttask.repository.IssueRepository;
import se.javagroup.projecttask.repository.TeamRepository;
import se.javagroup.projecttask.repository.UserRepository;
import se.javagroup.projecttask.repository.WorkItemRepository;
import se.javagroup.projecttask.repository.data.*;
import se.javagroup.projecttask.resource.dto.WorkItemDto;
import se.javagroup.projecttask.service.exception.BadInputException;
import se.javagroup.projecttask.service.exception.NotFoundException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Component
public final class Service {
    private final IssueRepository issueRepository;
    private final TeamRepository teamRepository;
    private final UserRepository userRepository;
    private final WorkItemRepository workItemRepository;

    public Service(IssueRepository issueRepository, TeamRepository teamRepository, UserRepository userRepository,
                   WorkItemRepository workItemRepository) {
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
        if (numberexists) {
            usernumber = randomizedUserNumber();
            numberexists = checkUserNumber(usernumber);
            user.setUserNumber(usernumber);
        }
        if (!numberexists) {
            user.setUserNumber(usernumber);
        }
        return userRepository.save(user);
    }


    public List<User> getAllUsers(String firstName, String lastName, String username, String teamName,
                                  String userNumber, String page, String limit) {

        Pageable pageable = null;
        if (page != null && limit != null) {
            pageable = PageRequest.of(Integer.valueOf(page), Integer.valueOf(limit));
        }

        return userRepository.findAllByQuery(firstName, lastName, username, teamName, userNumber, pageable);
    }

    public User getUserByUserNumber(Long userNumber) {
        Optional<User> userOptional = userRepository.findByUserNumber(userNumber);
        if (userOptional.isPresent()) {
            return userOptional.get();
        }
        throw new NotFoundException("User not found");
    }

    public User updateUser(Long userNumber, User user) {
        User foundUser = userRepository.findByUserNumber(userNumber).get();
        foundUser.setFirstName(user.getFirstName());
        foundUser.setLastName(user.getLastName());
        foundUser.setUsername(user.getUsername());
        foundUser.setStatus(user.isStatus());
        if (!foundUser.isStatus()) {
            Collection<WorkItem> foundWorkItems = workItemRepository.findWorkItemsByUserId(foundUser.getId());
            foundWorkItems.forEach(w -> workItemRepository
                    .save(new WorkItem(w.getId(), w.getDescription(), WorkItemStatus.UNSTARTED)));
        }
        return userRepository.save(foundUser);
    }

    public void deleteUser(Long userNumber) {
        Optional<User> userOptional = userRepository.findByUserNumber(userNumber);
        if (!userOptional.isPresent()) {
            throw new NotFoundException("User not found");
        }
        User user = userOptional.get();
        for (WorkItem w : user.getWorkitems()) {
            workItemRepository.save(new WorkItem(w.getId(), w.getDescription(), w.getWorkItemStatus(), null));
        }
        userRepository.delete(user);
    }

    public WorkItem createWorkItem(WorkItemDto workItem) {
        Optional<String> status = Optional.ofNullable(workItem.getWorkItemStatus());
        if (!status.isPresent()) {
            return workItemRepository.save(new WorkItem(null, workItem.getDescription(), WorkItemStatus.UNSTARTED));
        }
        validateStatus(workItem);

        WorkItem workItemEntity = new WorkItem(null, workItem.getDescription(), WorkItemStatus
                .valueOf(workItem.getWorkItemStatus().toUpperCase()));

        return workItemRepository.save(workItemEntity);
    }

    public List<WorkItem> getAllWorkItems(String status, boolean issue, String text, String user, String from, String to, String page, String limit) {

        Pageable pageable = null;
        if (page != null && limit != null) {
            pageable = PageRequest.of(Integer.valueOf(page), Integer.valueOf(limit));
        }

        Boolean issueBool = null;
        if (issue) {
            issueBool = true;
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");

        LocalDate fromDate = null;
        if (from != null) {
            fromDate = LocalDate.parse(from, formatter);
        }

        LocalDate toDate = null;
        if (to != null) {
            toDate = LocalDate.parse(to, formatter);
        }

        return workItemRepository.findAllWorkItemsByQuery(status, issueBool, text, user, fromDate, toDate, pageable);
    }

    public Collection<WorkItem> getAllWorkItemsForUser(Long userNumber) {
        Optional<User> userOptional = userRepository.findByUserNumber(userNumber);
        if (userOptional.isPresent()) {
            return workItemRepository.findWorkItemsByUserId(userOptional.get().getId());
        }
        throw new NotFoundException("User not found");
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
        throw new NotFoundException(String.format("Team with id %s was not found", teamId));
    }

    public WorkItem getWorkItem(Long workItemId) {
        Optional<WorkItem> workItem = workItemRepository.findById(workItemId);
        if (workItem.isPresent()) {
            return workItem.get();
        }
        throw new NotFoundException(String.format("WorkItem with id %s was not found", workItemId));
    }

    public WorkItem updateWorkItem(Long workItemId, WorkItemDto workItemNew, Long userNumber) {
        WorkItem workItem = validateWorkItem(workItemId);
        if (userExists(userNumber)) {
            User user = userRepository.findByUserNumber(userNumber).get();
            validateUserStatus(user);
            validateWorkItemSize(user);
            if (workItemNew == null) {
                return workItemRepository.save(new WorkItem(workItem.getId(), workItem.getDescription(),
                        workItem.getWorkItemStatus(), user));
            }
            validateStatus(workItemNew);
            return workItemRepository.save(new WorkItem(workItem.getId(), workItemNew.getDescription(),
                    WorkItemStatus.valueOf(workItemNew.getWorkItemStatus()), user));
        }
        if (workItemNew == null) {
            return workItem;
        }
        validateStatus(workItemNew);
        return workItemRepository.save(new WorkItem(workItem.getId(), workItemNew.getDescription(),
                WorkItemStatus.valueOf(workItemNew.getWorkItemStatus()),
                workItem.getUser()));
    }

    public void deleteWorkItem(Long workItemId) {
        Optional<WorkItem> workItemOptional = workItemRepository.findById(workItemId);
        if (!workItemOptional.isPresent()) {
            throw new NotFoundException("Work item not found");
        }
        WorkItem workItem = workItemOptional.get();
        workItem.setUser(null);
        workItemRepository.save(workItem);
        workItemRepository.delete(workItem);
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
        throw new NotFoundException("Team not found");
    }

    public Team updateTeam(Long teamId, Team team) {
        return teamRepository.findById(teamId).map(t -> teamRepository.save(team)).orElseThrow(
                () -> new NotFoundException(String.format("Team with id %s was not found", teamId)));
    }

    public User addUserToTeam(Long teamId, Long userNumber) {
        Optional<Team> teamOptional = teamRepository.findById(teamId);
        Optional<User> userOptional = userRepository.findByUserNumber(userNumber);
        if (!teamOptional.isPresent() || !userOptional.isPresent()) {
            throw new NotFoundException("Team or user doesn't exist");
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

    public void deleteTeam(Long teamId) {
        Optional<Team> team = teamRepository.findById(teamId);
        if (!team.isPresent()) {
            throw new NotFoundException("Team not found");
        }
        for (User u : team.get().getUsers()) {
            userRepository.save(new User(u.getId(), u.getFirstName(), u.getLastName(), u.getUsername(),
                    u.getUserNumber(), u.isStatus(), null));
        }
        teamRepository.delete(team.get());
    }

    public Optional<Issue> createIssue(Issue issue, Long workItemId) {
        Optional<WorkItem> foundWorkItem = workItemRepository.findById(workItemId);
        if (foundWorkItem.isPresent()) {
            WorkItem oldWorkItem = foundWorkItem.get();
            if (oldWorkItem.getWorkItemStatus().toString().equals("DONE")) {
                Optional<Issue> newIssue = Optional.of(issueRepository.save(new Issue(issue.getDescription(),
                        issue.getWorkItem())));
                workItemRepository.save(new WorkItem(oldWorkItem.getId(), oldWorkItem.getDescription(),
                        WorkItemStatus.UNSTARTED));
                return newIssue;
            }
        }
        throw new BadInputException("You can't create an issue if the workitem is unstarted or just started");
    }

    public Issue getIssue(Long issueId) {
        Optional<Issue> issueOptional = issueRepository.findById(issueId);
        if (!issueOptional.isPresent()) {
            throw new NotFoundException("Issue not found");
        }
        return issueOptional.get();
    }

    public Issue updateIssue(Long issueId, Issue issue) {
        return issueRepository.findById(issueId).map(i -> issueRepository.save(issue)).orElseThrow(
                () -> new NotFoundException(String.format("Issue with id %s was not found", issueId)));
    }

    public void deleteIssue(Long issueId) {
        Optional<Issue> issueOptional = issueRepository.findById(issueId);
        if (!issueOptional.isPresent()) {
            throw new NotFoundException("Issue not found");
        }
        issueRepository.delete(issueOptional.get());
    }

    private String validateUsernameLength(String username) {
        if (!(username.length() < 10)) return "valid";
        else throw new BadInputException("username must be 10 characters or more");
    }

    private void validateName(User user) {
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
        for (User u : userRepository.findAll()) {
            if (userNumber == u.getUserNumber()) {
                return true;
            }
        }
        return false;
    }

    private boolean teamIsFullTest(Team team) {
        if (team.getUsers() != null) {
            if (team.getUsers().size() >= 10) {
                return true;
            }
        }
        return false;
    }

    private void validateWorkItemSize(User user) {
        if (user.getWorkitems().size() >= 5) {
            throw new BadInputException("Maximum amount of workitems reached for user");
        }
    }

    private WorkItem validateWorkItem(Long workItemId) {
        Optional<WorkItem> workItem = workItemRepository.findById(workItemId);
        if (!workItem.isPresent()) {
            throw new NotFoundException("WorkItem not found");
        }
        return workItem.get();
    }

    private boolean userExists(Long userNumber) {
        if (userRepository.findByUserNumber(userNumber).isPresent()) {
            return true;
        }
        if (userNumber == 0) {
            return false;
        }
        throw new NotFoundException("User not found");
    }

    private void validateStatus(WorkItemDto workItem) {
        if (workItem.getWorkItemStatus() != null) {
            Optional.ofNullable(workItem.getWorkItemStatus())
                    .filter(status -> status.toUpperCase().equals("DONE") || status.toUpperCase().equals("UNSTARTED") ||
                            status.toUpperCase().equals("STARTED"))
                    .orElseThrow(() -> new BadInputException(workItem.getWorkItemStatus() + " - Wrong status type"));
        }
    }

    private void validateUserStatus(User user) {
        if (!user.isStatus()) {
            throw new BadInputException("Can not add work item to inactive user");
        }
    }
}