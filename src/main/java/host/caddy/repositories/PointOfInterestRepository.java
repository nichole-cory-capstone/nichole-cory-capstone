package host.caddy.repositories;

import host.caddy.models.Collection;
import host.caddy.models.PointOfInterest;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PointOfInterestRepository extends CrudRepository<PointOfInterest, Long>{
   PointOfInterest findOne (Long id);
   PointOfInterest findByPlaceId(String placeId);
   List<PointOfInterest> findAll();
   void delete(PointOfInterest deleted);

}
