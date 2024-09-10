package eu.ecodex.utils.configuration.testdata;

import eu.ecodex.utils.configuration.api.annotation.ConfigurationLabel;
import java.nio.file.Path;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Configuration properties for example ABC.
 *
 * <p>This class binds properties prefixed with "example.abc" from external configuration sources.
 */
@Data
@Component
@ConfigurationProperties(prefix = "example.abc")
public class MoreExampleProperties {
    private String address;
    @ConfigurationLabel("application-test.properties path")
    private Path path;
}
