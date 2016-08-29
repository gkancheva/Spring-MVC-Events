package eventSystem.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 30, unique = true)
    private String username;

    @Column(nullable = false, length = 60)
    private String passwordHash;

    @Column(nullable = false)
    private String eMail;

    @Column(nullable = false, length = 100)
    private String fullName;

    @Column(nullable = false)
    private String role;

    @OneToMany(mappedBy = "author")
    private Set<Event> events = new HashSet<>();

    public User() {
    }

    public User(String username, String passwordHash, String eMail, String fullName, String role) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.eMail = eMail;
        this.fullName = fullName;
        this.role = role;
    }

    public User(Long id, String username, String passwordHash, String eMail, String fullName, String role) {
        this(username, passwordHash, eMail, fullName, role);
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Set<Event> getEvents() {
        return events;
    }

    public void setEvents(Set<Event> events) {
        this.events = events;
    }

    public String geteMail() {
        return eMail;
    }

    public void seteMail(String eMail) {
        this.eMail = eMail;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return "user{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", eMail='" + eMail + '\'' +
                ", fullName='" + fullName + '\'' +
                '}';
    }
}
