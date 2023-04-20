package hexlet.code.domain.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class TaskStatusRequestDTO {

    @NotBlank(message = "Field 'name' must not be empty!")
    @Size(min = 1, message = "Status name length must be at least 1 characters!")
    private String name;

    public TaskStatusRequestDTO() {
    }

    public TaskStatusRequestDTO(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
