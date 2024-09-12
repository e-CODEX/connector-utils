package eu.ecodex.utils.monitor.gw.service;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import eu.ecodex.utils.monitor.app.MonitorAppConfiguration;
import eu.ecodex.utils.monitor.gw.GatewayMonitorAutoConfiguration;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.boot.web.client.RootUriTemplateHandler;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    classes = {GatewayMonitorAutoConfiguration.class, MonitorAppConfiguration.class}
)
@ActiveProfiles("test")
@Disabled("test needs external resource!")
class GatewayHealthIndicatorTest {
    @Autowired
    GatewayHealthIndicator gatewayHealthIndicator;
    @LocalServerPort
    int localPort;

    @Test
    void testGateway() {
        var restTemplateBuilder = new RestTemplateBuilder();
        var restTemplate = restTemplateBuilder
            .uriTemplateHandler(
                new RootUriTemplateHandler("http://localhost:" + localPort + "/actuator/health"))
            .basicAuthentication("test", "test")
            .build();

        var forEntity = restTemplate.getForEntity("/", String.class);

        assertThat(forEntity).isNotNull();

        System.out.println(forEntity.getBody());
    }
}
