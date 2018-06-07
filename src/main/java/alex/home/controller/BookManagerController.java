package alex.home.controller;

import alex.home.service.BookManagerService;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public class BookManagerController {

    private BookManagerService bookManagerService;

    @Autowired
    public BookManagerController(BookManagerService bookManagerService) {
        this.bookManagerService = bookManagerService;
    }

    @RequestMapping(value = "/bookManager-UpdateOffer/{offerId},{subEventId}", method = RequestMethod.GET)
    public Map<String, JSONObject> getBookManagerUpdateOfferRequests(@PathVariable("offerId") String offerId, @PathVariable("subEventId") String subEventId) {
        return bookManagerService.getUpdateOfferRequests(offerId, subEventId);
    }

    @RequestMapping(value = "/bookManager-GetOfferByUniqueId/{offerId},{subEventId}", method = RequestMethod.GET)
    public Map<String, String> getBookManagerGetOfferByUniqueIdRequests(@PathVariable("offerId") String offerId, @PathVariable("subEventId") String subEventId) {
        return bookManagerService.getOfferByUniqueIdRequests(offerId, subEventId);
    }
}
