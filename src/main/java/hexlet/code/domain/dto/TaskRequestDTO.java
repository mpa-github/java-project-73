package hexlet.code.domain.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class TaskRequestDTO {

    @NotBlank(message = "Field 'name' must not be empty!")
    private String name;

    private String description;

    //@NotNull (?)
    //private long authorId;

    private long executorId;

    @NotNull
    private long taskStatusId;

    public TaskRequestDTO() {
    }

    public TaskRequestDTO(String name,
                          String description,
                          long executorId,
                          long taskStatusId) {
        this.name = name;
        this.description = description;
        this.executorId = executorId;
        this.taskStatusId = taskStatusId;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public long getExecutorId() {
        return executorId;
    }

    public long getTaskStatusId() {
        return taskStatusId;
    }
}
