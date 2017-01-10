package com.crossover.trial.weather;

import com.crossover.trial.weather.model.AirportData;
import com.crossover.trial.weather.model.AtmosphericInformation;
import com.crossover.trial.weather.model.DataPoint;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.BDDAssertions.then;
import static org.junit.Assert.assertEquals;

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
    private Gson _gson = new Gson();
    private DataPoint _dp;

    @Before
    public void setUp() throws Exception {
        _dp = new DataPoint.Builder()
            .withCount(10).withFirst(10).withMedian(20).withLast(30).withMean(22).build();

        addAirport("BOS", 42.364347, -71.005181);
        addAirport("EWR", 40.6925, -74.168667);
        addAirport("JFK", 40.639751, -73.778925);
        addAirport("LGA", 40.777245, -73.872608);
        addAirport("MMU", 40.79935, -74.4148747);

        updateWeather("BOS", "wind", _dp);
    }

    @After
    public void cleaning() throws Exception {
       testDelete();
    }

    private void updateWeather(String iata, String radiusString, DataPoint dp) {
        testRestTemplate.postForObject("http://localhost:" + this.port + "/collect/weather/{iata}/{pointType}", dp, Void.class, iata, radiusString);
    }


    private void addAirport(String iata,
                            double latString,
                            double longString) {
        testRestTemplate.postForObject("http://localhost:" + this.port + "/collect/airport/{iata}/{lat}/{long}", null, Void.class, iata, latString, longString);
    }

    List<AtmosphericInformation> weather(String iata, String radiusString) {
        return testRestTemplate.exchange("http://localhost:" + this.port + "/query/weather/{iata}/{radius}", HttpMethod.GET, null, new ParameterizedTypeReference<List<AtmosphericInformation>>() {
        }, iata, radiusString).getBody();
    }

    String ping() {
        return testRestTemplate.getForObject(
            "http://localhost:" + this.port + "/query/ping", String.class);
    }

    Set getAirports() {
        return testRestTemplate.getForObject(
            "http://localhost:" + this.port + "/collect/airports", Set.class);
    }

    void delete(String iata) {
        testRestTemplate.delete(
            "http://localhost:" + this.port + "/collect/airport/{iata}", iata);
    }

    @Test
    public void shouldReturn200WhenSendingPing() throws Exception {
        @SuppressWarnings("rawtypes")
        ResponseEntity<String> entity = this.testRestTemplate.getForEntity(
            "http://localhost:" + this.port + "/collect/ping", String.class);
        then(entity.getStatusCode()).isEqualTo(HttpStatus.OK);
    }


    @Test
    public void testGet() throws Exception {
        List<AtmosphericInformation> ais = weather("BOS", "0");
        assertEquals(ais.get(0).getWind(), _dp);
    }

    @Test
    public void testGetNearby() throws Exception {
        // check datasize response
        updateWeather("JFK", "wind", _dp);
        _dp.setMean(40);
        updateWeather("EWR", "wind", _dp);
        _dp.setMean(30);
        updateWeather("LGA", "wind", _dp);
        List<AtmosphericInformation> ais = weather("JFK", "200");
        assertEquals(3, ais.size());

    }

    @Test
    public void testUpdate() throws Exception {

        DataPoint windDp = new DataPoint.Builder()
            .withCount(10).withFirst(10).withMedian(20).withLast(30).withMean(22).build();
        updateWeather("BOS", "wind", windDp);
        weather("BOS", "0");

        String ping = ping();
        JsonElement pingResult = new JsonParser().parse(ping);
        assertEquals(1, pingResult.getAsJsonObject().get("datasize").getAsInt());

        DataPoint cloudCoverDp = new DataPoint.Builder()
            .withCount(4).withFirst(10).withMedian(60).withLast(100).withMean(50).build();
        updateWeather("BOS", "cloudcover", cloudCoverDp);

        List<AtmosphericInformation> ais = (List<AtmosphericInformation>) weather("BOS", "0");
        assertEquals(ais.get(0).getWind(), windDp);
        assertEquals(ais.get(0).getCloudCover(), cloudCoverDp);
    }

    @Test
    public void testDelete() throws Exception {
        Set<String> airports = getAirports();
        for (String iata : airports) {
           delete(iata);
        }
        airports = getAirports();
        assertEquals(0, airports.size());
    }


}
