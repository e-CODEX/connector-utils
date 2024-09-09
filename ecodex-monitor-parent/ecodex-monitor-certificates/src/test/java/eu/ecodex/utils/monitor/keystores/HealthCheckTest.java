package eu.ecodex.utils.monitor.keystores;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles({"test", "dev"})
public class HealthCheckTest {
    public static final String USERNAME = "admin";
    public static final String PASSWORD = "admin";
    @LocalServerPort
    int localServerPort;
    private RestTemplate restTemplate;

    @BeforeEach
    public void initRestTemplate() {
        var restTemplateBuilder = new RestTemplateBuilder();

        this.restTemplate = restTemplateBuilder
            .basicAuthentication(USERNAME, PASSWORD)
            .build();
    }

    @Test
    @Disabled("not complete yet!")
    void testUrl() {
        System.out.println(
            "URL: http://admin:admin@localhost:" + localServerPort + "/actuator/health"
        );
    }
}
