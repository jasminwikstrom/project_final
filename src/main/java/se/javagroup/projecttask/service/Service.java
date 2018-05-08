package se.javagroup.projecttask.service;

import se.javagroup.projecttask.repository.IssueRepository;
import se.javagroup.projecttask.repository.TeamRepository;
import se.javagroup.projecttask.repository.UserRepository;
import se.javagroup.projecttask.repository.WorkItemRepository;
import se.javagroup.projecttask.repository.data.WorkItem;
import se.javagroup.projecttask.repository.data.Issue;

import se.javagroup.projecttask.repository.data.Team;

import java.util.Optional;

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

    public WorkItem createWorkItem(WorkItem workItem) {
        return workItemRepository.save(new WorkItem(workItem.getDescription(), workItem.getWorkItemStatus()));
    }

    public Team createTeam(Team team){
        return teamRepository.save(new Team(team.getName(), team.isStatus(), team.getTeamNumber()));
    }
    public Iterable<Team> getAllTeams(){
        return teamRepository.findAll();
    }/*
    public Team updateTeam(Long id, String name, boolean status, Long teamNumber){
        return teamRepository.findById(Long.valueOf(id)).map(team -> {
            team.setName(String.valueOf(name));
            team.setStatus(status);
            team.setTeamNumber(Long.valueOf(teamNumber));
            return teamRepository.save(team);
        }).orElseThrow(() -> new BadInputException("Team with id " + id + " was not found"));
    }*/
    public Optional<WorkItem> getWorkItem(Long id){
        return workItemRepository.findById(id);
    }

    public Issue createIssue(Issue issue){
        return issueRepository.save(new Issue(issue.getDescription(), issue.getWorkItem()));
    }
}
