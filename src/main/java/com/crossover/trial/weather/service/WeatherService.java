package com.crossover.trial.weather.service;

import com.crossover.trial.weather.exception.WeatherException;
import com.crossover.trial.weather.model.AirportData;
import com.crossover.trial.weather.model.AtmosphericInformation;
import com.crossover.trial.weather.model.DataPoint;
import com.crossover.trial.weather.model.DataPointType;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Mahdi on 1/10/2017.
 */
public interface WeatherService {
    void addDataPoint(String iataCode, String pointType, DataPoint dp) throws WeatherException;

    void addAirport(AirportData ad);

    AirportData addAirport(String iataCode, double latitude, double longitude);

    Map<String, Object> healthData();

    List<AtmosphericInformation> listOfMatchingAtmosphereInfo(String iata, double radius);

    Set<String> getAirports();

    void updateRequestFrequency(String iata, Double radius);

    AirportData findAirportData(String iataCode);

    int getAirportDataIdx(String iataCode);

    void deleteAirportData(String iataCode);
}
