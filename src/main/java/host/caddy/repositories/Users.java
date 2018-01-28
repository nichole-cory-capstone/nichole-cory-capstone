package host.caddy.repositories;

import host.caddy.models.User;
import org.springframework.data.repository.CrudRepository;

public interface Users extends CrudRepository<User, Long> {
    User findByEmail(String email);
}
