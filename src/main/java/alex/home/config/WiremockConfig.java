package alex.home.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "wiremockstudy")
@Data
public class WiremockConfig {
    private String endpoint;
    private int port;
}
