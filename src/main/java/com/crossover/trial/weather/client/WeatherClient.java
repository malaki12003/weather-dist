package com.crossover.trial.weather.client;

import com.crossover.trial.weather.model.DataPoint;
import org.springframework.web.client.RestTemplate;

/**
 * A reference implementation for the weather client. Consumers of the REST API can look at WeatherClient
 * to understand API semantics. This existing client populates the REST endpoint with dummy data useful for
 * testing.
 *
 * @author code test administrator
 */
public class WeatherClient {

    private static final String BASE_URI = "http://localhost:9090";

    /** end point for read queries */
    private  RestTemplate restTemplate=new RestTemplate();



    public void pingCollect() {
        System.out.print("collect.ping: " +restTemplate.getForObject(BASE_URI+"/collect/ping",String.class));
    }

    public void query(String iata) {
        System.out.println("query." + iata + ".0: " + restTemplate.getForObject(BASE_URI+"/query/weather/{iata}/0",String.class,iata));
    }

    public void pingQuery() {
        System.out.println("query.ping: " + restTemplate.getForObject(BASE_URI+"/query/ping",String.class));
    }

    public void populate(String pointType, int first, int last, int mean, int median, int count) {
        DataPoint dp = new DataPoint.Builder()
                .withFirst(first).withLast(last).withMean(mean).withMedian(median).withCount(count)
                .build();
        restTemplate.postForObject(BASE_URI+"/collect/weather/BOS/{pointType}",dp,Void.class,pointType);
    }

    public void exit() {
        try {
            restTemplate.getForObject(BASE_URI+"/collect/exit",Void.class);
        } catch (Throwable t) {
            // swallow
        }
    }

    public static void main(String[] args) {
        WeatherClient wc = new WeatherClient();
        wc.pingCollect();
        wc.populate("wind", 0, 10, 6, 4, 20);

        wc.query("BOS");
        wc.query("JFK");
        wc.query("EWR");
        wc.query("LGA");
        wc.query("MMU");

        wc.pingQuery();
        wc.exit();
        System.out.print("complete");
        System.exit(0);
    }
}
