package host.caddy.controllers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.errors.ApiException;
import com.google.maps.model.GeocodingResult;
import host.caddy.services.GMapsService;
import host.caddy.services.YelpSearch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
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

    @PostMapping("/search/google/geocode")
    public @ResponseBody String geocodeSearchPost(@RequestParam String address) throws IOException, InterruptedException, ApiException{
        return googleSearch.getGMapsJSON(googleSearch.geocodeAddress(address));
    }

//    @PostMapping("/search/google/places")
//    public @ResponseBody String geocodeSearchPost(@RequestParam String address) throws IOException, InterruptedException, ApiException{
//        GeoApiContext context = googleSearch.getContext();
//        return googleSearch.geocodeAddress(address, context);
//    }

}
