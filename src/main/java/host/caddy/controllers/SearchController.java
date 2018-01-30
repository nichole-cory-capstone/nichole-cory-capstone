package host.caddy.controllers;

import host.caddy.services.YelpSearch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
public class SearchController {

    @Autowired
    private YelpSearch yelpSearch;

    @PostMapping("/search/yelp")
    public @ResponseBody String yelpSearchPost(@RequestParam String name, @RequestParam String lat, @RequestParam String lon){
        return yelpSearch.byNameJSON(name,lat,lon);
    }

    @GetMapping("/search/yelp")
    public String yelpSearchGet(){
        return "temp";
    }

    @GetMapping("/search/guest")
    public String guestSearch(){
        return "guestsearch";
    }

    @GetMapping("/about")
    public String aboutGet(){
        return "aboutus";
    }

}
