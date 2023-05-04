package hexlet.code.domain.builder;

import hexlet.code.domain.model.Label;
import hexlet.code.domain.model.Task;
import hexlet.code.domain.model.TaskStatus;
import hexlet.code.domain.model.User;
import hexlet.code.service.LabelService;
import hexlet.code.service.TaskStatusService;
import hexlet.code.service.UserService;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TaskModelBuilder implements TaskBuilder {

    private final TaskStatusService statusService;
    private final LabelService labelService;
    private final UserService userService;
    private Task task;

    public TaskModelBuilder(TaskStatusService statusService,
                            LabelService labelService,
                            UserService userService) {
        this.statusService = statusService;
        this.labelService = labelService;
        this.userService = userService;
    }

    @Override
    public TaskBuilder setTask(Task newTask) {
        this.task = newTask;
        return this;
    }

    @Override
    public TaskBuilder setName(String name) {
        this.task.setName(name);
        return this;
    }

    @Override
    public TaskBuilder setDescription(String description) {
        if (description != null) {
            this.task.setDescription(description);
        }
        return this;
    }

    @Override
    public TaskBuilder setTaskStatus(Long taskStatusId) {
        TaskStatus status = statusService.findStatusById(taskStatusId);
        this.task.setTaskStatus(status);
        return this;
    }

    @Override
    public TaskBuilder setLabels(List<Long> labelIds) {
        if (labelIds != null) {
            List<Label> labels = labelService.findAllLabelsById(labelIds);
            this.task.setLabels(labels);
        }
        return this;
    }

    @Override
    public TaskBuilder setAuthor(String authorEmail) {
        User author = userService.findUserByEmail(authorEmail);
        this.task.setAuthor(author);
        return this;
    }

    @Override
    public TaskBuilder setExecutor(Long executorId) {
        if (executorId != null) {
            User executor = userService.findUserById(executorId);
            this.task.setExecutor(executor);
        }
        return this;
    }

    @Override
    public Task build() {
        return this.task;
    }
}
