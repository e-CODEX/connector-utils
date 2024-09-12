package eu.ecodex.utils.configuration.testdata.subpackage1;

import eu.ecodex.utils.configuration.api.annotation.ConfigurationDescription;
import eu.ecodex.utils.configuration.api.annotation.ConfigurationLabel;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Configuration properties for the example configuration.
 *
 * <p>This class encapsulates the configuration properties that are prefixed with
 * "example.configuration". It holds the text property and the number property, both of which are
 * mandatory.
 */
@Data
@Component
@ConfigurationProperties(prefix = "example.configuration")
@ConfigurationLabel("Example Configuration")
@ConfigurationDescription("Properties for the example configuration")
public class ExamplePropertiesConfig {
    @NotNull
    @ConfigurationLabel("A text")
    private String text;
    /**
     * a number which should be max 60
     */
    @NotNull
    @ConfigurationDescription("A number.........")
    @Max(60)
    private Integer number;
}
