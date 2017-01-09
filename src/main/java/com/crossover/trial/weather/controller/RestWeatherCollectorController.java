package com.crossover.trial.weather.controller;

import com.crossover.trial.weather.exception.WeatherException;
import com.crossover.trial.weather.model.AirportData;
import com.crossover.trial.weather.model.AtmosphericInformation;
import com.crossover.trial.weather.model.DataPoint;
import com.crossover.trial.weather.service.WeatherService;
import com.crossover.trial.weather.service.WeatherServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @Autowired
    private WeatherService weatherService;

    @RequestMapping(value = "/ping", method = RequestMethod.GET)
    public ResponseEntity<String> ping() {
        return new ResponseEntity("ready", HttpStatus.OK);
    }


    @RequestMapping(value = "/weather/{iata}/{pointType}", method = RequestMethod.POST)
    public ResponseEntity updateWeather(@PathVariable("iata") String iataCode,
                                        @PathVariable("pointType") String pointType,
                                        @RequestBody DataPoint datapointJson) {
        try {
            weatherService.addDataPoint(iataCode, pointType, datapointJson);
        } catch (WeatherException e) {
            e.printStackTrace();
        }
        return new ResponseEntity(HttpStatus.OK);
    }

    @RequestMapping(value = "/airports", method = RequestMethod.GET)
    public ResponseEntity<Set> getAirports() {
        return new ResponseEntity(weatherService.getAirports(), HttpStatus.OK);
    }

    @RequestMapping(value = "/collect/airport/{iata}", method = RequestMethod.GET)
    public ResponseEntity<AirportData> getAirport(@PathVariable("iata") String iata) {
        AirportData ad = weatherService.findAirportData(iata);
        return new ResponseEntity(ad, HttpStatus.OK);
    }


    @RequestMapping(value = "/airport/{iata}/{lat}/{long}", method = RequestMethod.POST)
    public ResponseEntity addAirport(@PathVariable("iata") String iata,
                                     @PathVariable("lat") String latString,
                                     @PathVariable("long") String longString) {
        weatherService.addAirport(iata, Double.valueOf(latString), Double.valueOf(longString));
        return new ResponseEntity(HttpStatus.OK);
    }


    @RequestMapping(value = "/airport/{iata}", method = RequestMethod.DELETE)
    public ResponseEntity deleteAirport(@PathVariable("iata") String iata) {
        weatherService.deleteAirportData(iata);
        return new ResponseEntity(HttpStatus.ACCEPTED);
    }


    @RequestMapping(value = "/exit", method = RequestMethod.GET)
    public ResponseEntity exit() {
        System.exit(0);
        return ResponseEntity.noContent().build();
    }


    @RequestMapping(value = "/airport", method = RequestMethod.POST)
    public ResponseEntity addAirport(@RequestBody AirportData airportData) {
        weatherService.addAirport(airportData);
        return new ResponseEntity(HttpStatus.CREATED);
    }


}
