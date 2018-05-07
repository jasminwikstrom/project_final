package se.javagroup.projecttask.service;

import se.javagroup.projecttask.repository.IssueRepository;
import se.javagroup.projecttask.repository.TeamRepository;
import se.javagroup.projecttask.repository.UserRepository;
import se.javagroup.projecttask.repository.WorkItemRepository;
<<<<<<< HEAD
import se.javagroup.projecttask.repository.data.WorkItem;
<<<<<<< HEAD
import se.javagroup.projecttask.repository.data.Issue;
=======
=======
import se.javagroup.projecttask.repository.data.Team;

import java.util.List;
>>>>>>> Team

import java.util.Optional;
>>>>>>> 89567e935bfed972069ac8fdc384e87c826e619f

@org.springframework.stereotype.Service
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

<<<<<<< HEAD

=======
<<<<<<< HEAD
>>>>>>> 89567e935bfed972069ac8fdc384e87c826e619f
    public WorkItem createWorkItem(WorkItem workItem) {
        return workItemRepository.save(new WorkItem(workItem.getDescription(), workItem.getWorkItemStatus()));
=======
    public Team addTeam(Team team){
        return teamRepository.save(team);
    }
    public List<Team> getAllTeams(){
        return teamRepository.getAllTeams();
    }
    public Team updateTeam (Team team){
        return teamRepository.save(team);
>>>>>>> Team
    }

    public Optional<WorkItem> getWorkItem(Long id){
        return workItemRepository.findById(id);
    }

    public Issue createIssue(Issue issue){
        return issueRepository.save(new Issue(issue.getDescription(), issue.getWorkItem()));
    }
}
