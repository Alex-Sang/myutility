package alex.home.service;

import alex.home.config.BookManagerConfig;
import com.fasterxml.jackson.databind.JsonNode;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPMessage;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URI;
import java.util.*;

@Service
public class BookManagerService {

    private RestTemplate restTemplate;
    private BookManagerConfig bookManagerConfig;

    @Autowired
    public BookManagerService(RestTemplate restTemplate, BookManagerConfig bookManagerConfig) {
        this.restTemplate = restTemplate;
        this.bookManagerConfig = bookManagerConfig;
    }

    private URI targetUrl(String path){
        return UriComponentsBuilder
                .fromUriString("http://" + bookManagerConfig.getEndpoint() + ":" + bookManagerConfig.getPort() + path)
                .build()
                .toUri();
    }

    public Map<String, JSONObject> getUpdateOfferRequests(String offerId, String subEventId){
        System.out.println("=================================================");
        System.out.println("Querying all UpdateOffer Requests for Offer ID = [" + offerId + "] and SubEventID = [" + subEventId + "]...");

        List<String> requestList = getRequestListFromAllRequests("book1:OfferId", offerId, "book1:SubEventId", subEventId, "UpdateOffer");
        Collections.reverse(requestList);

        Map<String, JSONObject> UpdateOffer_Requests = new HashMap<>();
        System.out.println("Total " + requestList.size() + " Request(s) found from Bookmaker API wiremock server...");
        for (int i = 0; i < requestList.size(); i++){
            SOAPBody updateOffer = getSoapBody(requestList.get(i));
            String status = updateOffer.getElementsByTagName("book1:Status").item(1).getTextContent();
            String overrideStatus = updateOffer.getElementsByTagName("book1:OverrideStatus").item(1).getTextContent();
            String winPrice = updateOffer.getElementsByTagName("book1:WWRetailReturn").item(1).getTextContent();
            String placePrice = updateOffer.getElementsByTagName("book1:PPRetailReturn").item(1).getTextContent();

            JSONObject offerDetails = new JSONObject();
            offerDetails.put("Status", status);
            offerDetails.put("OverrideStatus", overrideStatus);
            offerDetails.put("WinPrice", winPrice);
            offerDetails.put("PlacePrice", placePrice);

            UpdateOffer_Requests.put("UpdateOfferRequest_"+i, offerDetails);
        }

        return UpdateOffer_Requests;
    }

    public Map<String, String> getOfferByUniqueIdRequests(String offerId, String subEventId) {

        System.out.println("=================================================");
        System.out.println("Querying all GetOfferByUniqueId Requests for Offer ID = [" + offerId + "] and SubEventID = [" + subEventId + "]...");

        List<String> requestList = getRequestListFromAllRequests("tem:OfferId", offerId, "tem:SubEventId", subEventId, "GetOfferByUniqueId");

        System.out.println("Total " + requestList.size() + " Request(s) found from Bookmaker API wiremock server...");
        Map<String, String> GetOfferByUniqueId_Requests = new HashMap<>();
        GetOfferByUniqueId_Requests.put("NumberOfRequests", String.valueOf(requestList.size()));
        GetOfferByUniqueId_Requests.put("OfferID", offerId);
        GetOfferByUniqueId_Requests.put("SubEventID", subEventId);

        return GetOfferByUniqueId_Requests;
    }

    private List<String> getRequestListFromAllRequests(String offerIdTagName, String offerId, String subEventIdTagName, String subEventId, String requestName){

        List<JsonNode> allRequestIds = getAllRequestIds();
        List<String> bodyList = new ArrayList<>();
        String objectResponseEntity;

        for (JsonNode id : allRequestIds) {
            ResponseEntity<JsonNode> requests = restTemplate.getForEntity(targetUrl("/__admin/requests/" + id.asText()), JsonNode.class);
            JsonNode jsonResponseDefinition = requests.getBody().get("responseDefinition");
            if (jsonResponseDefinition.get("status").asText().equals(String.valueOf(200))) {
                objectResponseEntity = requests.getBody().get("request").get("body").textValue();
                bodyList.add(objectResponseEntity);
            }
        }

        SOAPBody soapBody;
        List<String> requestList = new ArrayList<>();

        for (String body: bodyList) {
            soapBody = getSoapBody(body);
            try{
                String requestBodyOfferId = soapBody.getElementsByTagName(offerIdTagName).item(0).getTextContent();
                String requestBodySubEventId = soapBody.getElementsByTagName(subEventIdTagName).item(0).getTextContent();

                if (!(requestBodyOfferId == null)){
                    if (requestBodyOfferId.equals(offerId) && requestBodySubEventId.equals(subEventId) && body.contains(requestName)){
                        requestList.add(body);
                    }
                }
            }catch (NullPointerException ignored) {
            }
        }
        return requestList;
    }

    private SOAPBody getSoapBody(String body) {
        SOAPBody soapBody = null;
        try {
            MessageFactory messageFactory = MessageFactory.newInstance();
            MimeHeaders headers = new MimeHeaders();
            headers.addHeader("Content-Type", "text/xml");

            InputStream inputStream = new ByteArrayInputStream(body.getBytes());
            SOAPMessage soapMessage = messageFactory.createMessage(headers, inputStream);
            soapBody = soapMessage.getSOAPBody();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return soapBody;
    }

    private List<JsonNode> getAllRequestIds() {
        ResponseEntity<JsonNode> responseEntity =
                restTemplate.getForEntity(targetUrl("/__admin/requests"), JsonNode.class);

        JsonNode bodyNode = responseEntity.getBody().get("requests");
        Iterator<JsonNode> elements = bodyNode.elements();
        List<JsonNode> ids = new ArrayList<>();

        while (elements.hasNext()) {
            JsonNode node = elements.next();
            JsonNode id = node.get("id");
            ids.add(id);
        }
        return ids;
    }
}
