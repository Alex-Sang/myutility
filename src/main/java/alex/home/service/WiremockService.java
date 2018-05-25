package alex.home.service;

import alex.home.config.RequestPayload;
import alex.home.config.WiremockConfig;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.*;

@Service
public class WiremockService {

    private static final Logger LOGGER = LoggerFactory.getLogger(WiremockService.class);
    private RestTemplate restTemplate;
    private WiremockConfig wiremockConfig;

    @Autowired
    public WiremockService(RestTemplate restTemplate, WiremockConfig wiremockConfig) {
        this.restTemplate = restTemplate;
        this.wiremockConfig = wiremockConfig;
    }

    private URI targetURI(String path){
        return UriComponentsBuilder
                .fromUriString("http://" + wiremockConfig.getEndpoint() + ":" + wiremockConfig.getPort() + path)
                .build()
                .toUri();
    }

    public ResponseEntity<Map<?,?>> getActionsQuery(RequestPayload requestPayload){

        ResponseEntity<String> response = restTemplate.postForEntity(targetURI("/actions"), requestPayload.getRequestPayload(),String.class);

        int responseStatus = response.getStatusCodeValue();
        String responseBody = response.getBody();

        LOGGER.info("Received Response Status Code is: " + responseStatus);
        LOGGER.info("Received Response Body Message is: " + responseBody);

        Map<String, String> actionRequest = new HashMap<>();
        actionRequest.put("StatusCode", String.valueOf(responseStatus));
        actionRequest.put("ResponseBody", responseBody);

        return new ResponseEntity<>(actionRequest, HttpStatus.OK);

    }
}
