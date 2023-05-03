package hexlet.code.domain.model;

import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

// TODO Use custom synchronization methods (addLabel / removeLabel) in bidirectional
@Entity @Table(name = "tasks")
public class Task {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotBlank
    private String name;

    private String description;

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER)
    private TaskStatus taskStatus;

    // Save and update labels, when save task
    @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "task_label",
        joinColumns = @JoinColumn(name = "task_id"),
        inverseJoinColumns = @JoinColumn(name = "label_id"))
    private List<Label> labels = new ArrayList<>(); // TODO (!) List or Set (?)

    @ManyToOne(fetch = FetchType.EAGER)
    private User author;

    @ManyToOne(fetch = FetchType.EAGER)
    private User executor;

    @CreationTimestamp
    private Instant createdAt;

    public Task() {
    }

    public Task(String name,
                String description,
                TaskStatus taskStatus,
                List<Label> labels,
                User author,
                User executor,
                Instant createdAt) {
        this.name = name;
        this.description = description;
        this.taskStatus = taskStatus;
        this.labels = labels;
        this.author = author;
        this.executor = executor;
        this.createdAt = createdAt;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TaskStatus getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(TaskStatus taskStatus) {
        this.taskStatus = taskStatus;
    }

    public List<Label> getLabels() {
        return labels;
    }

    public void setLabels(List<Label> labels) {
        this.labels = labels;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public User getExecutor() {
        return executor;
    }

    public void setExecutor(User executor) {
        this.executor = executor;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public static class Builder {

        private final String name;
        private final TaskStatus taskStatus;
        private final User author;
        private String description;
        private List<Label> labels;
        private User executor;
        private Instant createdAt;

        public Builder(final String name,
                       final TaskStatus taskStatus,
                       final User author) {
            this.name = name;
            this.taskStatus = taskStatus;
            this.author = author;
        }

        public Builder setDescription(final String newDescription) {
            this.description = newDescription;
            return this;

        }

        public Builder setLabels(final List<Label> newLabels) {
            this.labels = newLabels;
            return this;
        }

        public Builder setExecutor(final User newExecutor) {
            this.executor = newExecutor;
            return this;
        }

        public Task createTask() {
            return new Task(
                this.name,
                this.description,
                this.taskStatus,
                this.labels,
                this.author,
                this.executor,
                this.createdAt
            );
        }
    }
}
