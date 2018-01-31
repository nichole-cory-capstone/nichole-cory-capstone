package host.caddy.repositories;

import host.caddy.models.Collection;
import host.caddy.models.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CollectionRepository extends CrudRepository<Collection, Long> {
    Collection findOne (Long id);
    Collection findCollectionByOwner(User user);
    List<Collection> findAll();
    @Override
    void delete(Collection deleted);
}
