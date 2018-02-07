package host.caddy.controllers;

import host.caddy.exceptions.CollectionNotFound;
import host.caddy.models.Collection;
import host.caddy.models.PointOfInterest;
import host.caddy.models.User;
import host.caddy.repositories.CollectionRepository;
import host.caddy.repositories.PointOfInterestRepository;
import host.caddy.security.CollectionOwnerExpression;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;

@Controller
@SessionAttributes({"collection"})
public class UsersController {

    @Autowired
    CollectionRepository collectionRepository;

    @Autowired
    PointOfInterestRepository pointOfInterestRepository;

    @Autowired
    CollectionOwnerExpression collectionOwnerExpression;

    @GetMapping("/user/profile")
    public String profile(Model model, @AuthenticationPrincipal User owner) {
        model.addAttribute("collections", collectionRepository.findCollectionsByOwner(owner));
        return "users/profile";
    }

    @GetMapping("/user/trips")
    public String saved() {

        return "redirect:/user/profile";
    }

    @GetMapping("/user/trips/{id}")
    @PreAuthorize("@CollectionOwnerExpression.isOwner(principal, #id)")
    public String getTrip(@PathVariable Long id, Model model) {

        Collection collection = collectionRepository.findOne(id);

        if (!collectionRepository.existsById(id)) {
            throw new CollectionNotFound(String.format("Collection with ID %d cannot be found", id));
        }
            model.addAttribute("collection", collection);
            return "users/trip";
    }



    @PostMapping("/user/trips/save")
    public String saveTrip(@ModelAttribute(name = "collection") Collection collection,@AuthenticationPrincipal User owner){
        if (collection.getId() != null && collection.getOwner().getId() == owner.getId()){
            collectionRepository.save(collection);

        }else if (collection.getId() == null){
            collection.setOwner(owner);
            collectionRepository.save(collection);
        }
        return "redirect:/user/trips/{collection.id}";
    }



    @GetMapping("/user/trips/{id}/remove")
    @PreAuthorize("@CollectionOwnerExpression.isOwner(principal, #id)")
    public String removeTrip(@PathVariable Long id) {


        if (!collectionRepository.existsById(id)) {
            throw new CollectionNotFound(String.format("Collection with ID %d cannot be found", id));
        }
         collectionRepository.delete(id);

        return "redirect:/user/profile";
    }


    @PostMapping("/user/trips/poi")
    public @ResponseBody List<PointOfInterest> savePoint(@ModelAttribute(name = "collection") Collection collection, @RequestParam(name = "placeId")String placeId, @AuthenticationPrincipal User owner){

            List<PointOfInterest> pointOfInterestList = collection.getPointsOfInterest();

            HashMap<String, PointOfInterest> poiMap = new HashMap<>();
            for (PointOfInterest point : pointOfInterestList) {
               poiMap.put(point.getPlaceId(), point);
             }

             PointOfInterest point = pointOfInterestRepository.findByPlaceId(placeId);

            if (!pointOfInterestRepository.existsByPlaceId(placeId)) {
                System.out.println(!pointOfInterestRepository.existsByPlaceId(placeId));
                PointOfInterest newPoint = new PointOfInterest();
                newPoint.setPlaceId(placeId);
                pointOfInterestRepository.save(newPoint);
                newPoint = pointOfInterestRepository.findByPlaceId(placeId);
                pointOfInterestList.add(newPoint);
                collection.setPointsOfInterest(pointOfInterestList);
                saveTrip(collection, owner);
            } else if (!poiMap.containsKey(point.getPlaceId())) {
                System.out.println("new to collection");
                pointOfInterestList.add(point);
                collection.setPointsOfInterest(pointOfInterestList);
                saveTrip(collection, owner);
            }else{
                System.out.println("Nothing to do");
            }
            return collection.getPointsOfInterest();
    }


    @PostMapping("/user/trips/poi/remove")
    public @ResponseBody List<PointOfInterest> removePoint(@ModelAttribute(name = "collection") Collection collection, @RequestParam(name = "placeId")String placeId, @AuthenticationPrincipal User owner){

        List<PointOfInterest> pointOfInterestList = collection.getPointsOfInterest();

        HashMap<String, PointOfInterest> poiMap = new HashMap<>();
        for (PointOfInterest point : pointOfInterestList) {
            poiMap.put(point.getPlaceId(), point);
        }

        PointOfInterest point = pointOfInterestRepository.findByPlaceId(placeId);

        if (poiMap.containsKey(point.getPlaceId())) {

            for(int i = 0; i < pointOfInterestList.size(); i++){
                if (pointOfInterestList.get(i).getPlaceId().equals(point.getPlaceId()))
                pointOfInterestList.remove(i);
            }

            collection.setPointsOfInterest(pointOfInterestList);
            saveTrip(collection, owner);
        }
        return collection.getPointsOfInterest();
    }



}
