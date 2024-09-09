package eu.ecodex.utils.monitor.gw.service;

import static org.assertj.core.api.Assertions.assertThat;

import eu.ecodex.utils.monitor.gw.GatewayMonitorAutoConfiguration;
import eu.ecodex.utils.monitor.gw.domain.AccessPoint;
import eu.ecodex.utils.monitor.gw.dto.AccessPointStatusDTO;
import java.time.Duration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import test.server.ServerStarter;

@ExtendWith(SpringExtension.class)
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.NONE,
    classes = {GatewayMonitorAutoConfiguration.class}
)
@ActiveProfiles("test")
class GatewaysCheckerServiceITCase {
    private static final Logger LOGGER =
        LoggerFactory.getLogger(GatewaysCheckerServiceITCase.class);
    public static final String GATEWAY_STATUS_IS = "Gateway status is: [{}]";
    @Autowired
    GatewaysCheckerService gatewaysCheckerService;

    @Test
    void getGatewayStatus_serverCrtDoesNotMatchName() {

        var server1 = ServerStarter.startServer1();

        var accessPoint = new AccessPoint();
        accessPoint.setName("gw1");
        accessPoint.setEndpoint("https://localhost:" + ServerStarter.getServerPort(server1) + "/");

        var gatewayStatus = gatewaysCheckerService.getGatewayStatus(accessPoint);

        LOGGER.info(GATEWAY_STATUS_IS, gatewayStatus);

        assertThat(gatewayStatus.getFailures()).hasSize(1);
    }

    @Test
    void getGatewayStatus2_illegalClientCrt() {
        var server2 = ServerStarter.startServer2();

        var accessPoint = new AccessPoint();
        accessPoint.setName("gw2");
        accessPoint.setEndpoint("https://localhost:" + ServerStarter.getServerPort(server2) + "/");

        var gatewayStatus = gatewaysCheckerService.getGatewayStatus(accessPoint);

        LOGGER.info(GATEWAY_STATUS_IS, gatewayStatus);

        assertThat(gatewayStatus.getFailures()).hasSize(1);
    }

    @Test
    void getGatewayStatus3() {
        var server3 = ServerStarter.startServer3();

        var accessPoint = new AccessPoint();
        accessPoint.setName("gw3");
        accessPoint.setEndpoint("https://localhost:" + ServerStarter.getServerPort(server3) + "/");

        AccessPointStatusDTO gatewayStatus = gatewaysCheckerService.getGatewayStatus(accessPoint);

        LOGGER.info(GATEWAY_STATUS_IS, gatewayStatus);

        assertThat(gatewayStatus.getFailures()).isEmpty();
    }

    @Test
    void getGatewayStatus_recheck() throws InterruptedException {
        var server3 = ServerStarter.startServer3();

        var accessPoint = new AccessPoint();
        accessPoint.setName("gw3");
        accessPoint.setEndpoint("https://localhost:" + ServerStarter.getServerPort(server3) + "/");

        var gatewayStatus = gatewaysCheckerService.getGatewayStatus(accessPoint);
        LOGGER.info(GATEWAY_STATUS_IS, gatewayStatus);
        assertThat(gatewayStatus.getFailures()).isEmpty();

        LOGGER.info("sleep 8s");
        Thread.sleep(Duration.ofSeconds(8).toMillis());

        var gatewayStatus2 = gatewaysCheckerService.getGatewayStatus(accessPoint);
        LOGGER.info(GATEWAY_STATUS_IS, gatewayStatus2);
        assertThat(gatewayStatus).isNotEqualTo(gatewayStatus2);
    }

    @Test
    void getGatewayStatus4_illegalServerCrt() throws InterruptedException {
        var server4 = ServerStarter.startServer4();

        var accessPoint = new AccessPoint();
        accessPoint.setName("gw4");
        accessPoint.setEndpoint("https://localhost:" + ServerStarter.getServerPort(server4) + "/");

        Thread.sleep(Duration.ofSeconds(2).toMillis());

        var gatewayStatus = gatewaysCheckerService.getGatewayStatus(accessPoint);
        LOGGER.info(GATEWAY_STATUS_IS, gatewayStatus);

        assertThat(gatewayStatus.getFailures()).hasSize(1);

        var gatewayStatus2 = gatewaysCheckerService.getGatewayStatus(accessPoint);

        var gatewayStatus1 = gatewaysCheckerService.getGatewayStatus(accessPoint);
        assertThat(gatewayStatus).isSameAs(gatewayStatus1);
        assertThat(gatewayStatus).isSameAs(gatewayStatus2);
    }
}
