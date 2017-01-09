package com.crossover.trial.weather;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.BDDAssertions.then;
/**
 * Created by Mahdi on 1/10/2017.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = WeatherServer.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RestWeatherTests {
    @LocalServerPort
    private int port;



    @Autowired
    private TestRestTemplate testRestTemplate;

    @Test
    public void shouldReturn200WhenSendingPing() throws Exception {
        @SuppressWarnings("rawtypes")
        ResponseEntity<String> entity = this.testRestTemplate.getForEntity(
            "http://localhost:" + this.port + "/collect/ping", String.class);
        then(entity.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

}
