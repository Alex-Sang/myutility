package alex.home.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "bookManager")
@Data
public class BookManagerConfig {
    private String endpoint;
    private int port;
}
