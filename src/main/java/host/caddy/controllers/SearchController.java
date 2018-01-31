package host.caddy.controllers;

import com.google.maps.GeoApiContext;
import com.google.maps.errors.ApiException;
import com.google.maps.model.GeocodingResult;
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

    @GetMapping("/search")
    public String geocodeSearch(@RequestParam String location, Model model) throws IOException, InterruptedException, ApiException{
        if(location != null){
            GeoApiContext context = googleSearch.getContext();
            Collection collection = new Collection();
            GeocodingResult[] results = googleSearch.geocodeAddress(location, context);
            String latLng[] = results[0].geometry.location.toString().split(",");
            collection.setLatitude(latLng[0]);
            collection.setLongitude(latLng[1]);
            collection.setFormattedAddress(results[0].formattedAddress);
            model.addAttribute("goeApiContext",context);
            model.addAttribute("collection",collection);
            return "/search/guestsearch";
        }else {
            return "redirect:/";
        }
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
        System.out.println(googleSearch.geocodeAddress(address));
        return googleSearch.getGMapsJSON(googleSearch.geocodeAddress(address));
    }

}
