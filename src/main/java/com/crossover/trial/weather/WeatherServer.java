package com.crossover.trial.weather;


import com.crossover.trial.weather.client.WeatherClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.event.EventListener;


/**
 * This main method will be use by the automated functional grader. You shouldn't move this class or remove the
 * main method. You may change the implementation, but we encourage caution.
 *
 * @author code test administrator
 */
@SpringBootApplication
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
}
