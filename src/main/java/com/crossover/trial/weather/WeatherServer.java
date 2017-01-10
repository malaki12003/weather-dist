package com.crossover.trial.weather;


import com.crossover.trial.weather.client.WeatherClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.event.EventListener;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;


/**
 * This main method will be use by the automated functional grader. You shouldn't move this class or remove the
 * main method. You may change the implementation, but we encourage caution.
 *
 * @author code test administrator
 */
@SpringBootApplication
@EnableCaching
@EnableSwagger2
@ComponentScan(basePackages = {"com.crossover.trial.weather"}, excludeFilters={
    @ComponentScan.Filter(type= FilterType.ASSIGNABLE_TYPE, value=WeatherClient.class)})
public class WeatherServer {

    private final static Logger logger = LoggerFactory.getLogger(WeatherServer.class);


    @EventListener
    public void onStartup(ApplicationReadyEvent even) {
        logger.info("Weather Server started.");
    }

    public static void main(String[] args) {
        logger.info("Starting Weather App local testing server");
        SpringApplication.run(WeatherServer.class, args);

    }
    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
            .select()
            .apis(RequestHandlerSelectors.any())
            .paths(PathSelectors.any())
            .build();
    }
}
