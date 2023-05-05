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
    private boolean isTaskSet = false;
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
        this.isTaskSet = true;
        return this;
    }

    @Override
    public TaskBuilder setName(String name) {
        checkState();
        this.task.setName(name);
        return this;
    }

    @Override
    public TaskBuilder setDescription(String description) {
        checkState();
        if (description != null) {
            this.task.setDescription(description);
        }
        return this;
    }

    @Override
    public TaskBuilder setTaskStatus(Long taskStatusId) {
        checkState();
        TaskStatus status = statusService.findStatusById(taskStatusId);
        this.task.setTaskStatus(status);
        return this;
    }

    @Override
    public TaskBuilder setLabels(List<Long> labelIds) {
        checkState();
        if (labelIds != null) {
            List<Label> labels = labelService.findAllLabelsById(labelIds);
            this.task.setLabels(labels);
        }
        return this;
    }

    @Override
    public TaskBuilder setAuthor(String authorEmail) {
        checkState();
        User author = userService.findUserByEmail(authorEmail);
        this.task.setAuthor(author);
        return this;
    }

    @Override
    public TaskBuilder setExecutor(Long executorId) {
        checkState();
        if (executorId != null) {
            User executor = userService.findUserById(executorId);
            this.task.setExecutor(executor);
        }
        return this;
    }

    @Override
    public Task build() {
        this.isTaskSet = false;
        return this.task;
    }

    private void checkState() {
        if (!this.isTaskSet) {
            throw new IllegalStateException("Task is not set!");
        }
    }
}
