package host.caddy.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.*;
import javax.validation.constraints.Size;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Column(nullable = false)
    @NotBlank(message = "Your first name cannot be empty")
    private String firstname;

    @Column(nullable = false)
    @NotBlank(message = "Your last name cannot be empty")
    private String lastname;

    @Column(nullable = false)
    @NotBlank(message = "Your password cannot be empty")
    @Size(min = 8, message = "Your password should have at least 8 characters")
    @JsonIgnore
    private String password;

    @Column(nullable = false, unique = true)
    @Email(message = "Enter a valid email address")
    @NotBlank(message = "Enter an email")
    private String email;


    public User(User copy) {
        id = copy.id;
        email = copy.email;
        firstname = copy.firstname;
        lastname = copy.lastname;
        password = copy.password;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
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
}
