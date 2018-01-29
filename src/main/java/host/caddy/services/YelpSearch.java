package host.caddy.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import host.caddy.models.yelp.Yelp;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class YelpSearch {
    private Yelp yelp = new Yelp();
    private RestTemplate restTemplate = new RestTemplate();
    private MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
    private Map map = new HashMap<String, String>();
    private Gson gson = new Gson();

    @Value("${yelp-bearer-token}")
    private String bearer_token;



    public Yelp byName(String name, String lat, String lon) {
        query(name,lat,lon);
        return yelp;
    }

    public String byNameJSON(String name, String lat, String lon) {
        query(name,lat,lon);
        return gson.toJson(yelp, Yelp.class);
    }


    private String query(String name, String lat, String lon){


            map.put("Content-Type", "application/graphql");
            //Like map.put("Authorization", "Bearer <TOKEN>");
            map.put("Authorization", bearer_token);
            headers.setAll(map);

            String req_payload = "{search(term: \"" + name + "\",latitude: " + lat + ", longitude: " + lon + ", limit: 1, radius: 1600){ business {name id rating url display_phone price photos location{address1 city state zip_code country formatted_address} coordinates {latitude longitude}}}}";
            HttpEntity<?> request = new HttpEntity<>(req_payload, headers);
            String url = "https://api.yelp.com/v3/graphql";
            try {
                ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, request, String.class);
                if (response.getStatusCode() == HttpStatus.OK) {

                    ObjectMapper objectMapper = new ObjectMapper();
                    yelp = objectMapper.readValue(response.getBody(), Yelp.class);
                    return response.getStatusCode().toString();
                }else {
                    return response.getStatusCode().toString();
                }
            } catch (Exception e) {
                return e.getMessage();
            }
        }
    }
