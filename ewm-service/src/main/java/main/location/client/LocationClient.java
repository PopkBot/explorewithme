package main.location.client;

import client.BaseClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.util.DefaultUriBuilderFactory;

import java.util.Map;

@Component
public class LocationClient extends BaseClient {


    @Autowired
    public LocationClient(@Value("${location-geocoding-server.url}") String serverUrl,
                          RestTemplateBuilder builder) {
        super(builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl))
                .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                .build());
    }

    public ResponseEntity<Object> getCountryAndCityByCoords(Double lon, Double lat) {
        Map<String, Object> params = Map.of(
                "lon", lon,
                "lat", lat,
                "format", "json"
        );
        return get("?lon={lon}&lat={lat}&format={format}", params);

    }
}
