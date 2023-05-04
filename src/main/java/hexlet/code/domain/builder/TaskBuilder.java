package hexlet.code.domain.builder;

import hexlet.code.domain.dto.TaskRequestDTO;
import hexlet.code.domain.model.Label;
import hexlet.code.domain.model.Task;
import hexlet.code.domain.model.TaskStatus;
import hexlet.code.domain.model.User;
import hexlet.code.service.LabelService;
import hexlet.code.service.TaskStatusService;
import hexlet.code.service.UserService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TaskBuilder implements ModelBuilder<Task, TaskRequestDTO> {

    private final TaskStatusService statusService;
    private final LabelService labelService;
    private final UserService userService;

    public TaskBuilder(TaskStatusService statusService, LabelService labelService, UserService userService) {
        this.statusService = statusService;
        this.labelService = labelService;
        this.userService = userService;
    }


    @Override
    public Task create(final TaskRequestDTO dto, final UserDetails authDetails) {
        String name = dto.getName();
        String description = dto.getDescription();
        TaskStatus status = statusService.findStatusById(dto.getTaskStatusId());
        User author = userService.findUserByEmail(authDetails.getUsername());
        User executor = userService.findUserById(dto.getExecutorId());
        List<Label> labels = labelService.findAllLabelsById(dto.getLabelIds());
        return new Task.Builder(name, status, author)
            .setDescription(description)
            .setExecutor(executor)
            .setLabels(labels)
            .createTask();
    }

    @Override
    public Task update(final Task task, final TaskRequestDTO dto) {
        String name = dto.getName();
        TaskStatus status = statusService.findStatusById(dto.getTaskStatusId());

        task.setName(name);
        task.setTaskStatus(status);

        if (dto.getDescription() != null) {
            String description = dto.getDescription();
            task.setDescription(description);
        }
        if (dto.getExecutorId() != null) {
            User executor = userService.findUserById(dto.getExecutorId());
            task.setExecutor(executor);
        }
        if (dto.getLabelIds() != null) {
            List<Label> labels = labelService.findAllLabelsById(dto.getLabelIds());
            task.setLabels(labels);
        }
        return task;
    }
}
