package host.caddy.repositories;

import host.caddy.models.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsersRepository extends CrudRepository<User, Integer> {
    User findByUsername(String username);
}