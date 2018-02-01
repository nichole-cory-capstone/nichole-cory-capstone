package host.caddy.controllers;

import host.caddy.models.Collection;
import host.caddy.models.PointOfInterest;
import host.caddy.models.User;
import host.caddy.repositories.CollectionRepository;
import host.caddy.repositories.PointOfInterestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Controller
@SessionAttributes({"trip"})
public class UsersController {

    @Autowired
    CollectionRepository collectionRepository;

    @Autowired
    PointOfInterestRepository pointOfInterestRepository;

    @GetMapping("/user/profile")
    public String profile() {
        return "users/profile";
    }

    @GetMapping("/user/trips")
    public String saved() {

        return "users/userstrips";
    }

    @GetMapping("/user/trips/{id}")
    public String getTrip(@PathVariable long id, Model model) {
        Collection collection =  collectionRepository.findOne(id);
        model.addAttribute("points",collection.getPointsOfInterest());
        model.addAttribute("trip", collection);
        return "users/trip";
    }



    @PostMapping("/user/trips/save")
    public String saveTrip(@ModelAttribute(name = "trip") Collection collection){
        User owner = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        collection.setOwner(owner);
        collectionRepository.save(collection);
        return "finished";
    }

    @PostMapping("/user/trips/save_poi")
    public String savePoint(@ModelAttribute Collection collection, @RequestParam(name = "placeId")String placeId) {
        if (placeId != null) {
            List<PointOfInterest> pointOfInterestList = collection.getPointsOfInterest();
            PointOfInterest point = pointOfInterestRepository.findByPlaceId(placeId);
            if (point == null) {
                PointOfInterest newPoint = new PointOfInterest();
                newPoint.setPlaceId(placeId);
                pointOfInterestRepository.save(newPoint);
                collection.getPointsOfInterest().add(newPoint);
                saveTrip(collection);
            } else if (!pointOfInterestList.contains(point)) {
                collection.getPointsOfInterest().add(point);
                saveTrip(collection);
            }
        }else {
            return "PlaceId cannot be empty";
        }
       return "finished";
    }
}
