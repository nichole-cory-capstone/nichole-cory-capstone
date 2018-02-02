package host.caddy.security;

import host.caddy.models.Collection;
import host.caddy.models.User;
import host.caddy.repositories.CollectionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CollectionOwnerExpression {
    private CollectionRepository collections;

    @Autowired
    public CollectionOwnerExpression(CollectionRepository collections) {
        this.collections = collections;
    }

    public boolean isOwner(User principal, Long collectionId) {
        Collection collection = collections.findOne(collectionId);
        User owner = collection.getOwner();
        return owner != null && owner.getId() ==  principal.getId();
    }
}