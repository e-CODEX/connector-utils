package eu.ecodex.utils.monitor.gw.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

@Disabled("unfinished")
@SuppressWarnings("squid:S1135")
class ConfiguredGatewaysTest {
    ConfiguredGatewaysService configuredGateways;

    @BeforeEach
    public void beforeEach() {
        configuredGateways = new ConfiguredGatewaysService();
    }

    @Test
    void testUpdate() {
        // TODO check why this is empty
    }
}
