package main.location.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.util.DefaultUriBuilderFactory;

import java.util.Locale;
import java.util.Map;

/**
 * Клиент для отправки запроса на внешний сервер для получения названия страны и города по координатам.
 */
@Component
public class LocationClient {

    private final BaseClientAdapter baseClient;

    @Autowired
    public LocationClient(@Value("${location-geocoding-server.url}") String serverUrl,
                          RestTemplateBuilder builder) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAcceptLanguage(Locale.LanguageRange.parse("en"));
        this.baseClient = new BaseClientAdapter(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build(),
                headers
        );
    }

    public ResponseEntity<Object> getCountryAndCityByCoords(Double lon, Double lat) {
        Map<String, Object> params = Map.of(
                "lon", lon,
                "lat", lat,
                "format", "json"
        );
        return baseClient.get("?lon={lon}&lat={lat}&format={format}", params);

    }
}
