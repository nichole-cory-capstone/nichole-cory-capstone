package host.caddy.services;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.errors.ApiException;
import com.google.maps.model.GeocodingResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class GMapsService {

    @Value("${gmaps-api-key}")
    private String mapsApiKey;

    private Gson gson = new GsonBuilder().setPrettyPrinting().create();


    public GeocodingResult[] geocodeAddress (String address) throws IOException, InterruptedException, ApiException {
        return GeocodingApi.geocode(getContext(), address).await();
    }

    public GeocodingResult[] geocodeAddress (String address, GeoApiContext context) throws IOException, InterruptedException, ApiException {
        return GeocodingApi.geocode(context, address).await();
    }

    public GeoApiContext getContext(){
        GeoApiContext context = new GeoApiContext.Builder()
                .apiKey(mapsApiKey)
                .build();

        return context;
    }

    public String getGMapsJSON (Object results){
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(results);
    }

    public
}
