package study.toss.util.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import study.toss.util.formatter.LocalDateFormatter;
import study.toss.util.formatter.LocalDateTimeFormatter;

@Configuration
public class AppConfig {

    @Bean
    public LocalDateFormatter localDateFormatter() {
        return new LocalDateFormatter();
    }

    @Bean
    public LocalDateTimeFormatter localDateTimeFormatter() {
        return new LocalDateTimeFormatter();
    }
}
