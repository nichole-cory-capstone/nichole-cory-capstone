package host.caddy.controllers;

import host.caddy.services.YelpSearch;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
public class SearchController {

    @PostMapping("/search/yelp")
    public @ResponseBody String search(@RequestParam String name, @RequestParam String lat, @RequestParam String lon){
        return new YelpSearch().byNameJSON(name,lat,lon);
    }

}
