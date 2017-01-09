package com.crossover.trial.weather.controller;

import com.crossover.trial.weather.model.AtmosphericInformation;
import com.crossover.trial.weather.service.WeatherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * The Weather App REST endpoint allows clients to query, update and check health stats. Currently, all data is
 * held in memory. The end point deploys to a single container
 *
 * @author code test administrator
 */
@RestController
@RequestMapping(value = "/query")
public class RestWeatherQueryController {

    public final Logger logger = Logger.getLogger(this.getClass().getName());

    @Autowired
    private WeatherService weatherService;


    /**
     * Retrieve service health including total size of valid data points and request frequency information.
     *
     * @return health stats for the service as a string
     */
    @RequestMapping(value = "/ping", method = RequestMethod.GET)
    public Map<String, Object> ping() {
        return weatherService.healthData();
    }

    /**
     * Given a query in json format {'iata': CODE, 'radius': km} extracts the requested airport information and
     * return a list of matching atmosphere information.
     *
     * @param iata         the iataCode
     * @param radiusString the radius in km
     * @return a list of atmospheric information
     */
    @RequestMapping(value = "/weather/{iata}/{radius}", method = RequestMethod.GET)
    public ResponseEntity<List<AtmosphericInformation>> weather(@PathVariable("iata") String iata, @PathVariable("radius") String radiusString) {
        double radius = radiusString == null || radiusString.trim().isEmpty() ? 0 : Double.valueOf(radiusString);
        return new ResponseEntity(weatherService.listOfMatchingAtmosphereInfo(iata, radius), HttpStatus.OK);
    }


}
