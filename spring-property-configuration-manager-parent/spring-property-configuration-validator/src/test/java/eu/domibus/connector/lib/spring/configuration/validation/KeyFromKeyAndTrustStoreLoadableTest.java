package eu.domibus.connector.lib.spring.configuration.validation;

import static eu.domibus.connector.lib.spring.configuration.validation.ConstraintViolationSetHelper.printSet;
import static org.assertj.core.api.Assertions.assertThat;

import eu.domibus.connector.lib.spring.configuration.KeyAndKeyStoreAndTrustStoreConfigurationProperties;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class KeyFromKeyAndTrustStoreLoadableTest {
    private static Validator validator;
    private KeyAndKeyStoreAndTrustStoreConfigurationProperties props;

    @BeforeAll
    public static void beforeClass() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @BeforeEach
    public void setUp() {
        props = new KeyAndKeyStoreAndTrustStoreConfigurationProperties();

        props.setKeyStore(ConstraintViolationSetHelper.generateTestStore());
        props.setPrivateKey(ConstraintViolationSetHelper.generateTestKeyConfig());
        props.setTrustStore(ConstraintViolationSetHelper.generateTestStore());
    }

    @Test
    void isValid() {
        Set<ConstraintViolation<KeyAndKeyStoreAndTrustStoreConfigurationProperties>> validate =
            validator.validate(props);
        printSet(validate);
        assertThat(validate).isEmpty();
    }

    @Test
    void aliasNotNotReadable() {
        props.getPrivateKey().setAlias("WRONG_ALIAS");

        Set<ConstraintViolation<KeyAndKeyStoreAndTrustStoreConfigurationProperties>> validate =
            validator.validate(props);
        printSet(validate);
        assertThat(validate).hasSize(2);
    }

    @Test
    void aliasNotNotSet() {
        props.getPrivateKey().setAlias(null);

        Set<ConstraintViolation<KeyAndKeyStoreAndTrustStoreConfigurationProperties>> validate =
            validator.validate(props);
        printSet(validate);
        assertThat(validate).hasSize(1);
    }
}
