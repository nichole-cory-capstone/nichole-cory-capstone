package host.caddy.controllers;

import com.google.maps.errors.ApiException;
import host.caddy.models.Collection;
import host.caddy.services.GMapsService;
import host.caddy.services.YelpSearch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;


@Controller
public class SearchController {
    @Value("${gmaps-api-key}")
    String mapsApiKey;

    @Autowired
    private YelpSearch yelpSearch;

    @Autowired
    private GMapsService googleSearch;

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

    @GetMapping("/search/user")
    public String userSearch(Model model){
        Collection collection = new Collection();
        model.addAttribute("trip", collection);
        return "usersearch"; }

    @PostMapping("/search/google/geocode")
    public @ResponseBody String geocodeSearchPost(@RequestParam String address) throws IOException, InterruptedException, ApiException{
        return googleSearch.getGMapsJSON(googleSearch.geocodeAddress(address));
    }

}
