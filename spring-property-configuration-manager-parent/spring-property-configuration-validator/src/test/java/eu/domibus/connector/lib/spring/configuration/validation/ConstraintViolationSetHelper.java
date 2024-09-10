package eu.domibus.connector.lib.spring.configuration.validation;

import eu.domibus.connector.lib.spring.configuration.KeyConfigurationProperties;
import eu.domibus.connector.lib.spring.configuration.StoreConfigurationProperties;
import java.util.Set;
import javax.validation.ConstraintViolation;
import org.springframework.core.io.ClassPathResource;

public class ConstraintViolationSetHelper {
    public static <T> void printSet(Set<ConstraintViolation<T>> constraintViolationSet) {
        constraintViolationSet.forEach(c -> System.out.println(
            "propertyPath: " + c.getPropertyPath() + " msg: " + c.getMessage()));
    }

    public static StoreConfigurationProperties generateTestStore() {
        StoreConfigurationProperties storeConfigurationProperties =
            new StoreConfigurationProperties();
        storeConfigurationProperties.setPath(new ClassPathResource("keystores/client-bob.jks"));
        storeConfigurationProperties.setPassword("12345");
        return storeConfigurationProperties;
    }

    public static KeyConfigurationProperties generateTestKeyConfig() {
        KeyConfigurationProperties keyConfigurationProperties = new KeyConfigurationProperties();
        keyConfigurationProperties.setAlias("bob");
        keyConfigurationProperties.setPassword("");
        return keyConfigurationProperties;
    }
}
