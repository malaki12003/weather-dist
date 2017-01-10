package com.crossover.trial.weather.service;

import com.crossover.trial.weather.exception.WeatherException;
import com.crossover.trial.weather.model.AirportData;
import com.crossover.trial.weather.model.AtmosphericInformation;
import com.crossover.trial.weather.model.DataPoint;
import com.crossover.trial.weather.model.DataPointType;
import com.crossover.trial.weather.util.Util;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created by Mahdi on 1/10/2017.
 */
@Service
public class WeatherServiceImpl implements WeatherService {


    /**
     * all known airports
     */
    private List<AirportData> airportData = new ArrayList<>();


    /**
     * Internal performance counter to better understand most requested information, this map can be improved but
     * for now provides the basis for future performance optimizations. Due to the stateless deployment architecture
     * we don't want to write this to disk, but will pull it off using a REST request and aggregate with other
     * performance metrics
     */
    public Map<AirportData, Integer> requestFrequency = new HashMap<AirportData, Integer>();

    public Map<Double, Integer> radiusFreq = new HashMap<Double, Integer>();

    /**
     * Update the airports weather data with the collected data.
     *
     * @param iataCode  the 3 letter IATA code
     * @param pointType the point type {@link DataPointType}
     * @param dp        a datapoint object holding pointType data
     * @throws WeatherException if the update can not be completed
     */

    @CacheEvict("healthData")
    @Override
    public void addDataPoint(String iataCode, String pointType, DataPoint dp) throws WeatherException {
        AtmosphericInformation atmosphericInformation = findAirportData(iataCode).getAtmosphericInformation();
        atmosphericInformation.update(DataPointType.valueOf(pointType.toUpperCase()), dp);
    }

    @CacheEvict("healthData")
    @Override
    public void addAirport(AirportData ad) {
        airportData.add(ad);
    }




    /**
     * Add a new known airport to our list.
     *
     * @param iataCode  3 letter code
     * @param latitude  in degrees
     * @param longitude in degrees
     * @return the added airport
     */

    @CacheEvict("healthData")
    @Override
    public AirportData addAirport(String iataCode, double latitude, double longitude) {
        AirportData ad = new AirportData();
        airportData.add(ad);
        ad.setIata(iataCode);
        ad.setLatitude(latitude);
        ad.setLatitude(longitude);
        return ad;
    }

    @Cacheable("healthData")
    @Override
    public Map<String, Object> healthData() {
        Map<String, Object> retval = new HashMap<>();

        int datasize = 0;
        for (AirportData ad : airportData) {
            AtmosphericInformation ai = ad.getAtmosphericInformation();
            // we only count recent readings
            if (ai.getCloudCover() != null
                || ai.getHumidity() != null
                || ai.getPressure() != null
                || ai.getPrecipitation() != null
                || ai.getTemperature() != null
                || ai.getWind() != null) {
                // updated in the last day
                if (ai.getLastUpdateTime() > System.currentTimeMillis() - 86400000) {
                    datasize++;
                }
            }
        }
        retval.put("datasize", datasize);

        Map<String, Double> freq = new HashMap<>();
        // fraction of queries
        for (AirportData data : airportData) {
            double frac = (double) requestFrequency.getOrDefault(data, 0) / requestFrequency.size();
            freq.put(data.getIata(), frac);
        }
        retval.put("iata_freq", freq);

        int m = radiusFreq.keySet().stream()
            .max(Double::compare)
            .orElse(1000.0).intValue() + 1;

        int[] hist = new int[m];
        for (Map.Entry<Double, Integer> e : radiusFreq.entrySet()) {
            int i = e.getKey().intValue() % 10;
            hist[i] += e.getValue();
        }
        retval.put("radius_freq", hist);

        return retval;
    }

    @Override
    public List<AtmosphericInformation> listOfMatchingAtmosphereInfo(String iata, double radius) {
        updateRequestFrequency(iata, radius);
        List<AtmosphericInformation> retval = new ArrayList<>();
        if (radius == 0) {
            AirportData airportData = findAirportData(iata);
            retval.add(airportData.getAtmosphericInformation());
        } else {
            AirportData ad = findAirportData(iata);
            for (int i = 0; i < airportData.size(); i++) {
                if (Util.calculateDistance(ad, airportData.get(i)) <= radius) {
                    AtmosphericInformation ai = airportData.get(i).getAtmosphericInformation();
                    if (ai.getCloudCover() != null || ai.getHumidity() != null || ai.getPrecipitation() != null
                        || ai.getPressure() != null || ai.getTemperature() != null || ai.getWind() != null) {
                        retval.add(ai);
                    }
                }
            }
        }
        return retval;
    }


    @Override
    public Set<String> getAirports() {
        Set<String> retval = new HashSet<>();
        for (AirportData ad : airportData) {
            retval.add(ad.getIata());
        }
        return retval;
    }

    /**
     * Records information about how often requests are made
     *
     * @param iata   an iata code
     * @param radius query radius
     */
    @CacheEvict("healthData")
    @Override
    public void updateRequestFrequency(String iata, Double radius) {
        AirportData airportData = findAirportData(iata);
        requestFrequency.put(airportData, requestFrequency.getOrDefault(airportData, 0) + 1);
        radiusFreq.put(radius, radiusFreq.getOrDefault(radius, 0) + 1);
    }

    /**
     * Given an iataCode find the airport data
     *
     * @param iataCode as a string
     * @return airport data or null if not found
     */
    @Override
    public AirportData findAirportData(String iataCode) {
        return airportData.stream()
            .filter(ap -> ap.getIata().equals(iataCode))
            .findFirst().orElse(null);
    }

    /**
     * Given an iataCode find the airport data
     *
     * @param iataCode as a string
     * @return airport data or null if not found
     */
    @Override
    public int getAirportDataIdx(String iataCode) {
        AirportData ad = findAirportData(iataCode);
        return airportData.indexOf(ad);
    }

    @CacheEvict("healthData")
    @Override
    public void deleteAirportData(String iataCode) {
        AirportData ad = findAirportData(iataCode);
        int idx = airportData.indexOf(ad);
        airportData.remove(ad);
    }


    /**
     * Haversine distance between two airports.
     *
     * @param ad1 airport 1
     * @param ad2 airport 2
     * @return the distance in KM
     */
    public double calculateDistance(AirportData ad1, AirportData ad2) {
        return Util.calculateDistance(ad1, ad2);
    }

    /**
     * A dummy init method that loads hard coded data
     */
//    @PostConstruct
//    private void init() {
//        airportData.clear();
//        atmosphericInformation.clear();
//        requestFrequency.clear();
//
//        addAirport("BOS", 42.364347, -71.005181);
//        addAirport("EWR", 40.6925, -74.168667);
//        addAirport("JFK", 40.639751, -73.778925);
//        addAirport("LGA", 40.777245, -73.872608);
//        addAirport("MMU", 40.79935, -74.4148747);
//    }
}
