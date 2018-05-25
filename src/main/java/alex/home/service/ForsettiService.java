package alex.home.service;

import alex.home.config.ForsettiConfig;

import com.fasterxml.jackson.databind.JsonNode;
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
public class ForsettiService {

    private RestTemplate restTemplate;
    private ForsettiConfig forsettiConfig;

    @Autowired
    public ForsettiService(RestTemplate restTemplate, ForsettiConfig forsettiConfig) {
        this.restTemplate = restTemplate;
        this.forsettiConfig = forsettiConfig;
    }

    private URI targetUrl(String path){
        return UriComponentsBuilder
                .fromUriString("http://" + forsettiConfig.getEndpoint() + ":" + forsettiConfig.getPort() + path)
                .build()
                .toUri();
    }

    public Map<String, String> getForsettiRequests(String offerId){

        System.out.println("Querying all UpdateOffer Requests for [" + offerId + "]...");

        List<JsonNode> allRequestIds = getAllRequestIds();
        Map<String, String> forsettiRequests = new HashMap<>();
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
                String requestBodyOfferId = soapBody.getElementsByTagName("a:OfferId").item(0).getTextContent();

                if (!(requestBodyOfferId == null)){
                    if (requestBodyOfferId.equals(offerId) && body.contains("UpdateOffer")){
                        requestList.add(body);
                    }
                }
            }catch (NullPointerException ignored) {
            }
        }
        Collections.reverse(requestList);

        for (int i = 0; i < requestList.size(); i++){
            forsettiRequests.put("UpdateOfferRequest_"+i, requestList.get(i));
        }
        return forsettiRequests;
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
