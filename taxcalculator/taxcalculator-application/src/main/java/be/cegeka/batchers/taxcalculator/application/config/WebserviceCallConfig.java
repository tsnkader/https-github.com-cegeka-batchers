package be.cegeka.batchers.taxcalculator.application.config;

import be.cegeka.batchers.taxcalculator.infrastructure.config.PropertyPlaceHolderConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;

@Configuration
@Import({PropertyPlaceHolderConfig.class})
@PropertySource("classpath:taxcalculator-application.properties")
public class WebserviceCallConfig {

    @Value(value = "${taxservice.url:/taxservice}")
    String taxServiceUrl;

    @Value(value = "${reset.url:/reset}")
    String resetUrl;

    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setMessageConverters(Arrays.<HttpMessageConverter<?>>asList(
                new MappingJackson2HttpMessageConverter(),
                new StringHttpMessageConverter()
        ));
        return restTemplate;
    }

    @Bean
    public String taxServiceUrl() {
        return taxServiceUrl;
    }

    @Bean
    public String resetUrl() {
        return resetUrl;
    }

}
