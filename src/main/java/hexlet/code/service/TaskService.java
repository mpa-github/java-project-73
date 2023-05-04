package hexlet.code.service;

import com.querydsl.core.types.Predicate;
import hexlet.code.domain.builder.TaskBuilder;
import hexlet.code.domain.dto.TaskRequestDTO;
import hexlet.code.domain.model.Task;
import hexlet.code.exception.NotFoundException;
import hexlet.code.exception.NotTheOwnerException;
import hexlet.code.repository.TaskRepository;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final TaskBuilder taskBuilder;

    public TaskService(TaskRepository taskRepository, TaskBuilder taskBuilder) {
        this.taskRepository = taskRepository;
        this.taskBuilder = taskBuilder;
    }

    public List<Task> findTasksByParams(Predicate predicate) {
        if (predicate == null) {
            return taskRepository.findAllByOrderByIdAsc();
        }
        return taskRepository.findAll(predicate, Sort.by(Sort.Direction.ASC, "id"));
    }

    public Task findTaskById(long id) {
        return taskRepository.findTaskById(id)
            .orElseThrow(() -> new NotFoundException("Task with id='%d' not found!".formatted(id)));
    }

    public Task createTask(TaskRequestDTO dto, UserDetails authDetails) {
        Task newTask = taskBuilder.create(dto, authDetails);
        return taskRepository.save(newTask);
    }

    public Task updateTask(long id, TaskRequestDTO dto, UserDetails authDetails) {
        Task existedTask = findTaskById(id);
        validateOwnerByEmail(existedTask.getAuthor().getEmail(), authDetails);
        Task updatedTask = taskBuilder.update(existedTask, dto);
        return taskRepository.save(updatedTask);
    }

    public void deleteTask(long id, UserDetails authDetails) {
        Task existedTask = findTaskById(id);
        validateOwnerByEmail(existedTask.getAuthor().getEmail(), authDetails);
        taskRepository.delete(existedTask);
    }

    // TODO We can use @PreAuthorize instead
    private void validateOwnerByEmail(String userEmail, UserDetails authDetails) {
        String authenticatedEmail = authDetails.getUsername();
        if (!authenticatedEmail.equalsIgnoreCase(userEmail)) {
            throw new NotTheOwnerException("Access denied. For owner only!");
        }
    }
}
