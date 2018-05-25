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

    @RequestMapping(value = "/forsetti-requests-all/{offerId}", method = RequestMethod.GET)
    public Map<String, String> getForsettiRequestsQuery(@PathVariable("offerId") String offerId) {
        return forsettiService.getForsettiRequests(offerId);
    }

    @RequestMapping(value = "/forsetti-request-initial/{offerId}", method = RequestMethod.GET)
    public Map<String, String> getForsettiRequestsQuery_Initial(@PathVariable("offerId") String offerId) {
        return forsettiService.getForsettiInitialRequestDetails(offerId);
    }
}
