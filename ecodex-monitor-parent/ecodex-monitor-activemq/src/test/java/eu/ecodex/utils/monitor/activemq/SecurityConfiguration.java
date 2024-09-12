package eu.ecodex.utils.monitor.activemq;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {
    @Bean
    protected SecurityFilterChain activeMQActuatorFilterChain(HttpSecurity http) throws Exception {
        return http
            .authorizeHttpRequests(auth -> {
                auth.requestMatchers("/actuator/**");
                auth.anyRequest().hasAnyRole("MONITOR");
            })
            .httpBasic(basic -> basic.realmName("actuator"))
            .build();
    }
}
