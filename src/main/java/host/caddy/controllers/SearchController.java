package host.caddy.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.maps.GeoApiContext;
import com.google.maps.errors.ApiException;
import com.google.maps.model.GeocodingResult;
import com.google.maps.model.PlacesSearchResponse;
import host.caddy.models.Collection;
import host.caddy.services.GMapsService;
import host.caddy.services.YelpSearch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Optional;


@Controller
@SessionAttributes({"goeApiContext","collection"})
public class SearchController {
    @Value("${gmaps-api-key}")
    String mapsApiKey;

    @Autowired
    private YelpSearch yelpSearch;

    @Autowired
    private GMapsService googleSearch;

    @PostMapping("/search/yelp")
    public @ResponseBody String yelpSearchPlace(@RequestParam String name, @RequestParam String lat, @RequestParam String lon){
        return yelpSearch.byNameJSON(name,lat,lon);
    }

    @GetMapping("/search")
    public String geocodeSearch(@RequestParam(required = false) String location , Model model) throws IOException, InterruptedException, ApiException {
        if (location == null) {
            location = "New York City";
        }
        GeoApiContext context = googleSearch.getContext();
        Collection collection = new Collection();

        GeocodingResult[] results = googleSearch.geocodeAddress(location, context);
        PlacesSearchResponse nearbyResults = googleSearch.nearbySearch(context, googleSearch.getLatLng(results[0]));
        String nearbyJSON = googleSearch.getGMapsJSON(nearbyResults.results);
        String latLng[] = results[0].geometry.location.toString().split(",");
        collection.setLatitude(latLng[0]);
        collection.setLongitude(latLng[1]);
        collection.setFormattedAddress(results[0].formattedAddress);

        model.addAttribute("nextPageToken", nearbyResults.nextPageToken);
        model.addAttribute("nearBy", nearbyJSON);
        model.addAttribute("goeApiContext", context);
        model.addAttribute("collection", collection);

        return "/search/search";
    }

    @PostMapping("/search/next")
    public @ResponseBody String nextPageSearch(@ModelAttribute("goeApiContext") GeoApiContext context, @RequestParam String pageToken) throws IOException, InterruptedException, ApiException{
        if(pageToken != null) {
            return googleSearch.getGMapsJSON(googleSearch.nearbyNextSearch(context, pageToken));
        }else{
            return "Error: pageToken cannot be false";
        }
    }


//    @PostMapping("/search/geocode")
//    public @ResponseBody String geocodeSearchPost(@RequestParam String address) throws IOException, InterruptedException, ApiException{
//        return googleSearch.getGMapsJSON(googleSearch.geocodeAddress(address));
//    }

}
