package hexlet.code.domain.model;

import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.Instant;

@Entity @Table(name = "labels")
public class Label {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String name;

    //@ManyToMany(mappedBy = "labels") // TODO unidirectional or bidirectional (?)
    //private List<Task> tasks = new ArrayList<>(); // TODO (!) List or Set (?)

    @CreationTimestamp
    private Instant createdAt;

    public Label() {
    }

    public Label(String name, Instant createdAt) {
        this.name = name;
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

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
