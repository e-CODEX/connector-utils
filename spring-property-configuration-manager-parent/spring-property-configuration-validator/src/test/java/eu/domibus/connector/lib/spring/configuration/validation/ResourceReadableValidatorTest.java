package eu.domibus.connector.lib.spring.configuration.validation;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import lombok.Data;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

public class ResourceReadableValidatorTest {
    private static Validator validator;

    @BeforeAll
    public static void setUp() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    void testResourceIsNull_shouldNotValidate() {
        TestEntity t = new TestEntity();

        Set<ConstraintViolation<TestEntity>> validate = validator.validate(t);

        validate.forEach(
            a -> System.out.println(a.getMessage())
        );
        assertThat(validate).hasSize(1);
    }

    @Test
    void testResourceConfiguredPathDoesNotExist_shouldNotValidate() {
        Resource res = new FileSystemResource("/dhjafjkljadflkjdaskldfaskjhdfs");
        TestEntity t = new TestEntity();
        t.setResource(res);

        Set<ConstraintViolation<TestEntity>> validate = validator.validate(t);

        validate.forEach(
            a -> System.out.println(a.getMessage())
        );
        assertThat(validate).hasSize(1);
    }

    /**
     * TestEntity is a static inner class that represents an entity with a single property,
     * 'resource'. The 'resource' field is annotated with the @CheckResourceIsReadable
     * annotation to ensure the resource is readable and valid.
     */
    @Data
    public static class TestEntity {
        @CheckResourceIsReadable
        Resource resource;
    }
}
