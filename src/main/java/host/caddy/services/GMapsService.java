package host.caddy.services;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.errors.ApiException;
import com.google.maps.model.GeocodingResult;
import com.google.maps.model.LatLng;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class GMapsService {

    @Value("${gmaps-api-key}")
    private String mapsApiKey;

    private Gson gson = new GsonBuilder().setPrettyPrinting().create();


    public GeocodingResult[] geocodeAddress (String address) throws IOException, InterruptedException, ApiException {
        return GeocodingApi.newRequest(getContext()).address(address).await();
    }


    public GeocodingResult[] geocodeAddress (String address, GeoApiContext context) throws IOException, InterruptedException, ApiException {
        return GeocodingApi.newRequest(context).address(address).await();
    }

    public GeocodingResult[] geocodePlace (String placeId,GeoApiContext context) throws IOException, InterruptedException, ApiException {

        return GeocodingApi.newRequest(context).place(placeId).await();
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


    public LatLng  getLatLng(GeocodingResult result){
               LatLng latLng = new LatLng();
               latLng.lat = result.geometry.location.lat;
               latLng.lat = result.geometry.location.lng;
              return latLng;
        }
}
