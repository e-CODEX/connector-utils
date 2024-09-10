package eu.ecodex.utils.configuration.testdata.subpackage2;

import eu.ecodex.utils.configuration.testdata.subpackage1.ExamplePropertiesConfig;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.stereotype.Component;

/**
 * Configuration properties for the application, with a prefix "com.example.dc".
 *
 * <p>This class provides properties for the application configuration, including a nested
 * configuration for additional settings via ExamplePropertiesConfig.
 */
@Data
@Component
@ConfigurationProperties(prefix = "com.example.dc")
public class Prop {
    private String abc;
    private String dgf;
    @NestedConfigurationProperty
    private ExamplePropertiesConfig examplePropertiesConfig = new ExamplePropertiesConfig();
}
