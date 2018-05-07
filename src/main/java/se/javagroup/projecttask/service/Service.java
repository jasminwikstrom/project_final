package se.javagroup.projecttask.service;

import se.javagroup.projecttask.repository.IssueRepository;
import se.javagroup.projecttask.repository.TeamRepository;
import se.javagroup.projecttask.repository.UserRepository;
import se.javagroup.projecttask.repository.WorkItemRepository;
<<<<<<< HEAD
import se.javagroup.projecttask.repository.data.WorkItem;
=======
import se.javagroup.projecttask.repository.data.Issue;
>>>>>>> issue

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
    public WorkItem createWorkItem(WorkItem workItem) {
        return workItemRepository.save(new WorkItem(workItem.getDescription(), workItem.getWorkItemStatus()));
    }
=======
    public Issue createIssue(Issue issue){
        return issueRepository.save(new Issue(issue.getDescription(), issue.getWorkItem()));
    }

>>>>>>> issue
}
