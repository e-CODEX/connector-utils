package eu.ecodex.utils.spring.converter;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.Resource;

@SuppressWarnings("squid:S1135")
public class ResourceConverterTest {
    private ResourceConverter resourceConverter;

    @BeforeAll
    public static void beforeAll() throws IOException {
        try {
            Path path = Paths.get("./target/testfile");
            Files.createFile(path);
        } catch (FileAlreadyExistsException e) {
            // TODO check why this body is empty
        }
    }

    @BeforeEach
    public void initConverter() {
        this.resourceConverter = new ResourceConverter();
    }

    @Test
    void testClasspathResource() {
        Resource convert = resourceConverter.convert("classpath:/META-INF/spring.factories");
        assertThat(convert).isNotNull();
    }

    @Test
    void testFileResource1() {
        Resource convert = resourceConverter.convert("file://./target/testfile");
        assertThat(convert).isNotNull();
    }

    @Test
    void testFileResource2() {
        Resource convert = resourceConverter.convert("file://./target/testfile");
        assertThat(convert).isNotNull();
    }

    @Test
    void testFileResource3() {
        Resource convert = resourceConverter.convert("file:./target/testfile");
        assertThat(convert).isNotNull();
    }

    @Test
    void testFileResource4() {
        Resource convert = resourceConverter.convert("file:C:/test/testfile");
        assertThat(convert).isNotNull();
    }

    @Test
    void testUrlResource() {
        Resource convert = resourceConverter.convert("http://www.example.com/nonexisting");
        assertThat(convert).isNotNull();
    }
}
