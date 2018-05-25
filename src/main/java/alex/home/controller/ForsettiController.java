package alex.home.controller;

import alex.home.service.ForsettiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public class ForsettiController {

    private ForsettiService forsettiService;

    @Autowired
    public ForsettiController(ForsettiService forsettiService) {
        this.forsettiService = forsettiService;
    }

    @RequestMapping(value = "/forsetti-request/{offerId}", method = RequestMethod.GET)
    public Map<String, String> getForsettiRequestsQuery(@PathVariable("offerId") String eventId) {
        return forsettiService.getForsettiRequests(eventId);
    }
}