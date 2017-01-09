package com.crossover.trial.weather.controller;

import com.crossover.trial.weather.WeatherException;
import com.crossover.trial.weather.model.AirportData;
import com.crossover.trial.weather.model.AtmosphericInformation;
import com.crossover.trial.weather.model.DataPoint;
import com.crossover.trial.weather.model.DataPointType;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

/**
 * A REST implementation of the WeatherCollector API. Accessible only to airport weather collection
 * sites via secure VPN.
 *
 * @author code test administrator
 */
@RestController
@RequestMapping(value = "/collect")
public class RestWeatherCollectorController {
    public final static Logger LOGGER = Logger.getLogger(RestWeatherCollectorController.class.getName());


    @RequestMapping(value = "/ping", method = RequestMethod.GET)
    public ResponseEntity<String> ping() {
        return new ResponseEntity("ready", HttpStatus.OK);
    }


    @RequestMapping(value = "/weather/{iata}/{pointType}", method = RequestMethod.POST)
    public ResponseEntity updateWeather(@PathVariable("iata") String iataCode,
                                        @PathVariable("pointType") String pointType,
                                        @RequestBody DataPoint datapointJson) {
        try {
            addDataPoint(iataCode, pointType, datapointJson);
        } catch (WeatherException e) {
            e.printStackTrace();
        }
        return new ResponseEntity(HttpStatus.OK);
    }

    @RequestMapping(value = "/airports", method = RequestMethod.GET)
    public ResponseEntity<List> getAirports() {
        Set<String> retval = new HashSet<>();
        for (AirportData ad : RestWeatherQueryController.airportData) {
            retval.add(ad.getIata());
        }
        return new ResponseEntity(retval, HttpStatus.OK);
    }

    @RequestMapping(value = "/collect/airport/{iata}", method = RequestMethod.GET)
    public ResponseEntity<AirportData> getAirport(@PathVariable("iata") String iata) {
        AirportData ad = RestWeatherQueryController.findAirportData(iata);
        return new ResponseEntity(ad, HttpStatus.OK);
    }


    @RequestMapping(value = "/airport/{iata}/{lat}/{long}", method = RequestMethod.POST)
    public ResponseEntity addAirport(@PathVariable("iata") String iata,
                                     @PathVariable("lat") String latString,
                                     @PathVariable("long") String longString) {
        addAirport(iata, Double.valueOf(latString), Double.valueOf(longString));
        return new ResponseEntity(HttpStatus.OK);
    }


    @RequestMapping(value = "/airport/{iata}", method = RequestMethod.DELETE)
    public ResponseEntity deleteAirport(@PathVariable("iata") String iata) {
        return new ResponseEntity(HttpStatus.NOT_IMPLEMENTED);
    }


    @RequestMapping(value = "/exit", method = RequestMethod.GET)
    public ResponseEntity exit() {
        System.exit(0);
        return ResponseEntity.noContent().build();
    }
    //
    // Internal support methods
    //

    /**
     * Update the airports weather data with the collected data.
     *
     * @param iataCode  the 3 letter IATA code
     * @param pointType the point type {@link DataPointType}
     * @param dp        a datapoint object holding pointType data
     * @throws WeatherException if the update can not be completed
     */
    public void addDataPoint(String iataCode, String pointType, DataPoint dp) throws WeatherException {
        int airportDataIdx = RestWeatherQueryController.getAirportDataIdx(iataCode);
        AtmosphericInformation ai = RestWeatherQueryController.atmosphericInformation.get(airportDataIdx);
        updateAtmosphericInformation(ai, pointType, dp);
    }

    /**
     * update atmospheric information with the given data point for the given point type
     *
     * @param ai        the atmospheric information object to update
     * @param pointType the data point type as a string
     * @param dp        the actual data point
     */
    public void updateAtmosphericInformation(AtmosphericInformation ai, String pointType, DataPoint dp) throws WeatherException {
        final DataPointType dptype = DataPointType.valueOf(pointType.toUpperCase());
        switch (dptype) {
            case WIND:
                if (dp.getMean() >= 0) {
                    ai.setWind(dp);
                    ai.setLastUpdateTime(System.currentTimeMillis());
                }
                break;
            case TEMPERATURE:
                if (dp.getMean() >= -50 && dp.getMean() < 100) {
                    ai.setTemperature(dp);
                    ai.setLastUpdateTime(System.currentTimeMillis());
                }
                break;
            case HUMIDTY:
                if (dp.getMean() >= 0 && dp.getMean() < 100) {
                    ai.setHumidity(dp);
                    ai.setLastUpdateTime(System.currentTimeMillis());
                }
                break;
            case PRESSURE:
                if (dp.getMean() >= 650 && dp.getMean() < 800) {
                    ai.setPressure(dp);
                    ai.setLastUpdateTime(System.currentTimeMillis());
                }
                break;
            case CLOUDCOVER:
                if (dp.getMean() >= 0 && dp.getMean() < 100) {
                    ai.setCloudCover(dp);
                    ai.setLastUpdateTime(System.currentTimeMillis());
                }
                break;
            case PRECIPITATION:
                if (dp.getMean() >= 0 && dp.getMean() < 100) {
                    ai.setPrecipitation(dp);
                    ai.setLastUpdateTime(System.currentTimeMillis());
                }
                break;
            default:
                throw new IllegalStateException("couldn't update atmospheric data");
        }
    }

    /**
     * Add a new known airport to our list.
     *
     * @param iataCode  3 letter code
     * @param latitude  in degrees
     * @param longitude in degrees
     * @return the added airport
     */
    public static AirportData addAirport(String iataCode, double latitude, double longitude) {
        AirportData ad = new AirportData();
        RestWeatherQueryController.airportData.add(ad);

        AtmosphericInformation ai = new AtmosphericInformation();
        RestWeatherQueryController.atmosphericInformation.add(ai);
        ad.setIata(iataCode);
        ad.setLatitude(latitude);
        ad.setLatitude(longitude);
        return ad;
    }
}
