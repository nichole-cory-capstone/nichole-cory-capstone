package host.caddy.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.List;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="user_id")
    private int id;

    @Column(nullable = false)
    @NotBlank(message = "Your password cannot be empty")
    @Size(min = 8, message = "Your password should have at least 8 characters")
    @JsonIgnore
    private String password;

    @Column(nullable = false)
    @Email(message = "Enter a valid email address")
    @NotBlank(message = "Enter an email")
    private String email;

    @Column(nullable = false, unique = true)
    @NotBlank(message = "Enter a username")
    private String username;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "owner")
    private List<Image> images;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "owner")
    private List<Collection> collections;

    public User(User copy) {
        id = copy.id;
        username = copy.username;
        email = copy.email;
        password = copy.password;
    }

    public User() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<Image> getImages() {
        return images;
    }

    public void setImages(List<Image> images) {
        this.images = images;
    }

    public List<Collection> getCollections() {
        return collections;
    }

    public void setCollections(List<Collection> collections) {
        this.collections = collections;
    }
}
