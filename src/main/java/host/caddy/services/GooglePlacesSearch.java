package host.caddy.services;

import com.google.maps.GeoApiContext;
import org.springframework.beans.factory.annotation.Value;

public class GooglePlacesSearch {
    @Value("${gmaps-api-key}")
    private String mapsApiKey;
    private GeoApiContext context = new GeoApiContext.Builder()
            .apiKey(mapsApiKey)
            .build();


}
