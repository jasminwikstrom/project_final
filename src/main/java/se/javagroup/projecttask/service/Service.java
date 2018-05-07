package se.javagroup.projecttask.service;

import se.javagroup.projecttask.repository.IssueRepository;
import se.javagroup.projecttask.repository.TeamRepository;
import se.javagroup.projecttask.repository.UserRepository;
import se.javagroup.projecttask.repository.WorkItemRepository;
import se.javagroup.projecttask.repository.data.User;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

import static java.util.stream.Collectors.toList;

@org.springframework.stereotype.Service
public final class Service {

    private final IssueRepository issueRepository;
    private final TeamRepository teamRepository;
    private final UserRepository userRepository;
    private final WorkItemRepository workItemRepository;
    private static final AtomicLong ids = new AtomicLong(1000);

    public Service(IssueRepository issueRepository, TeamRepository teamRepository, UserRepository userRepository, WorkItemRepository workItemRepository) {
        this.issueRepository = issueRepository;
        this.teamRepository = teamRepository;
        this.userRepository = userRepository;
        this.workItemRepository = workItemRepository;
    }

    public User createUser(User user) {

        return userRepository.add(new User(ids.incrementAndGet(),
                user.getFirstName(), user.getLastName(), user.getUsername(), user.getUserNumber(), user.isStatus()));
    }

    public Optional<User> getUser(Long id) {
        return userRepository.get(id);
    }

    public List<User> getAllUsers() {
        return userRepository.getAll().collect(toList());
    }

    public User updateUser(User user) {

        return userRepository.update(user);
    }

    public Optional<User> deleteCustomer(Long id) {
        return userRepository.delete(id);
    }


}
