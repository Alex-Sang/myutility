package alex.home.config;

import org.springframework.stereotype.Component;

@Component
public class RequestPayload {
    private String requestPayload;

    public String getRequestPayload() {
        return requestPayload;
    }

    public void setRequestPayload(String requestPayload) {
        this.requestPayload = requestPayload;
    }
}
