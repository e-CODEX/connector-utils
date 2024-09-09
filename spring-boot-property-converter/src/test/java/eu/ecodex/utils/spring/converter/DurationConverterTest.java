package eu.ecodex.utils.spring.converter;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DurationConverterTest {
    private DurationConverter durationConverter;

    @BeforeEach
    public void initConverter() {
        this.durationConverter = new DurationConverter();
    }

    @Test
    void testConvertMs() {
        Duration converted = durationConverter.convert("200ms");
        assertThat(converted).isEqualTo(Duration.ofMillis(200));
    }

    @Test
    void testConvertSeconds() {
        Duration converted = durationConverter.convert("200s");
        assertThat(converted).isEqualTo(Duration.ofSeconds(200));
    }

    @Test
    void testConvertMinutes() {
        Duration converted = durationConverter.convert("200m");
        assertThat(converted).isEqualTo(Duration.ofMinutes(200));
    }

    @Test
    void testConvertHours() {
        Duration converted = durationConverter.convert("20h");
        assertThat(converted).isEqualTo(Duration.ofHours(20));
    }

    @Test
    void testConvertDays() {
        Duration converted = durationConverter.convert("20d");
        assertThat(converted).isEqualTo(Duration.ofDays(20));
    }

    @Test
    void testDurationFormat_3Days() {
        Duration converted = durationConverter.convert("P3D");
        assertThat(converted).isEqualTo(Duration.ofDays(3));
    }

    @Test
    void testDurationFormat_4Hours20Minutes() {
        Duration converted = durationConverter.convert("PT4H20M");
        assertThat(converted).isEqualTo(Duration.ofHours(4).plusMinutes(20));
    }
}
