package eu.domibus.connector.lib.spring.configuration.validation;

import static org.assertj.core.api.Assertions.assertThat;

import eu.domibus.connector.lib.spring.configuration.StoreConfigurationProperties;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.Set;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

public class StoreLoadableValidatorTest {
    private static Validator validator;

    @BeforeAll
    public static void setUp() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    void isValid() {
        StoreConfigurationProperties storeConfigurationProperties =
            new StoreConfigurationProperties();

        storeConfigurationProperties.setPassword("12345");
        storeConfigurationProperties.setPath(
            new ClassPathResource("keystores/connector-backend.jks"));

        Set<ConstraintViolation<StoreConfigurationProperties>> validate =
            validator.validate(storeConfigurationProperties);

        assertThat(validate).isEmpty();
    }

    @Test
    void isValid_wrongPassword_shouldNotBeValid() {
        StoreConfigurationProperties storeConfigurationProperties =
            new StoreConfigurationProperties();

        storeConfigurationProperties.setPassword("WRONG");
        storeConfigurationProperties.setPath(
            new ClassPathResource("keystores/connector-backend.jks"));

        Set<ConstraintViolation<StoreConfigurationProperties>> validate =
            validator.validate(storeConfigurationProperties);

        validate.forEach(c -> System.out.println(c.getMessage()));

        assertThat(validate).hasSize(2);
    }

    @Test
    void isValid_wrongPATH_shouldNotBeValid() {
        StoreConfigurationProperties storeConfigurationProperties =
            new StoreConfigurationProperties();

        storeConfigurationProperties.setPassword("12345");
        storeConfigurationProperties.setPath(
            new ClassPathResource("keystores/NONEXISTANT_KEYSTORE.jks"));

        Set<ConstraintViolation<StoreConfigurationProperties>> validate =
            validator.validate(storeConfigurationProperties);
        validate.forEach(c -> System.out.println(
            "propertyPath: " + c.getPropertyPath() + " msg: " + c.getMessage()));

        assertThat(validate).hasSize(2);
    }
}
