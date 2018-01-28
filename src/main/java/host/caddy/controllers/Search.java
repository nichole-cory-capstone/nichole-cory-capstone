package host.caddy.controllers;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Controller
public class Search {

    @PostMapping("/search/yelp")
    public @ResponseBody String search(@RequestParam String name, @RequestParam String lat, @RequestParam String lon){
        RestTemplate restTemplate = new RestTemplate();
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        Map map = new HashMap<String, String>();
        map.put("Content-Type", "application/graphql");
        map.put("Authorization", "Bearer SOmPECWYjuUJavKVazVl9GRrX9XJF0uaSe3n7361trmT2e_5BCgJucbNTg-o-VurkN2c60qZqOSylvtRrmw1njqDnEVZvzyZNShCMHXYE9U542-Fe4O3uJPFFkRtWnYx");
        headers.setAll(map);
        String req_payload = "{search(term: \"" + name + "\",latitude: " + lat + ", longitude: " + lon + ", limit: 1, radius: 1600){ business {name rating id url phone}}}";
        HttpEntity<?> request = new HttpEntity<>(req_payload, headers);
        String url = "https://api.yelp.com/v3/graphql";

        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, request, String.class);
            if (response.getStatusCode() == HttpStatus.OK) {
                System.out.println(response.getBody());
                return response.getBody();
            } else {
                System.out.println("oops");
                return response.getStatusCode().toString();
            }
        }catch (Exception e){
            System.out.println(e);
            return e.getMessage();
        }

//        YelpQuery.query("CodeUp", "29.4267857","-98.48957639999998")
    }

    @GetMapping("/search/yelp")
    public @ResponseBody String test(){
        return "temp";
    }
}
