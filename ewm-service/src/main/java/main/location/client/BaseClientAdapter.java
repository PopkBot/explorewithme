package main.location.client;

import client.BaseClient;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

public class BaseClientAdapter extends BaseClient {


    public BaseClientAdapter(RestTemplate rest, HttpHeaders additionalHeaders) {
        super(rest, additionalHeaders);
    }

    public BaseClientAdapter(RestTemplate rest) {
        super(rest);
    }

    @Override
    public ResponseEntity<Object> get(String path, Map<String, Object> params){
        return super.get(path,params);
    }
}
