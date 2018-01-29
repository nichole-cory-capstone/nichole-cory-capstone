package host.caddy.controllers;

import host.caddy.services.YelpSearch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
public class SearchController {

    @Autowired
    private YelpSearch yelpSearch;

    @PostMapping("/search/yelp")
    public @ResponseBody String search(@RequestParam String name, @RequestParam String lat, @RequestParam String lon){
        return yelpSearch.byNameJSON(name,lat,lon);
    }

}
