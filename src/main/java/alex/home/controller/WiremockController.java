package alex.home.controller;

import alex.home.config.RequestPayload;
import alex.home.service.WiremockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

@RestController
public class WiremockController {
    private WiremockService wiremockService;
    private RequestPayload requestPayload;

    @Autowired
    public WiremockController(WiremockService wiremockService, RequestPayload requestPayload) {
        this.wiremockService = wiremockService;
        this.requestPayload = requestPayload;
    }

    @RequestMapping(value = "/wiremock-request", method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<?, ?>> getActionQuery(@RequestBody String postPayload) {
        requestPayload.setRequestPayload(postPayload);
        return wiremockService.getActionsQuery(requestPayload);
    }
}
