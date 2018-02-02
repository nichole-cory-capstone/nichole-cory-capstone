package host.caddy.controllers;

import host.caddy.models.Collection;
import host.caddy.models.PointOfInterest;
import host.caddy.models.User;
import host.caddy.repositories.CollectionRepository;
import host.caddy.repositories.PointOfInterestRepository;
import host.caddy.security.CollectionOwnerExpression;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
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

    @Autowired
    CollectionOwnerExpression collectionOwnerExpression;

    @GetMapping("/user/profile")
    public String profile(Model model) {
        model.addAttribute("collections",collectionRepository.findAll());
        return "users/profile";
    }

    @GetMapping("/user/trips")
    public String saved() {

        return "redirect:/users/profile";
    }

    @GetMapping("/user/trips/{id}")
    @PreAuthorize("@CollectionOwnerExpression.isOwner(principal, #id)")
    public String getTrip(@PathVariable long id, Model model) {
            Collection collection = collectionRepository.findOne(id);
            model.addAttribute("points", collection.getPointsOfInterest());
            model.addAttribute("collection", collection);
            return "users/trip";
    }



    @PostMapping("/user/trips/save")
    @PreAuthorize("isAuthenticated()")
    public String saveTrip(@ModelAttribute(name = "trip") Collection collection){
        User owner = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        collection.setOwner(owner);
        collectionRepository.save(collection);
        return "redirect:/user/trips/{collection.id}";
    }

    @PostMapping("/user/trips/save_poi")
//    @PreAuthorize("@CollectionOwnerExpression.isOwner(principal, #id)")
    public @ResponseBody String savePoint(@ModelAttribute Collection collection, @RequestParam(name = "placeId")String placeId) {
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
       return "PoI added successfully";
    }
}
