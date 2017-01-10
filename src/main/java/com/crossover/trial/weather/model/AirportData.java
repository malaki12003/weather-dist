package com.crossover.trial.weather.model;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * Basic airport information.
 *
 * @author code test administrator
 */
public class AirportData {

    private String name;
    // 3-letter FAA code or IATA code (blank or "" if not assigned)
    private String iata;
    // Main city served by airport. May be spelled differently from name.
    private String city;
    // Country or territory where airport is located.
    private String country;
    // 4-letter ICAO code (blank or "" if not assigned)
    private String icao;
    // latitude value in degrees
    private double latitude;
    // longitude value in degrees
    private double longitude;
    // In feet
    private double altitude;

    // Hours offset from UTC. Fractional hours are expressed as decimals. (e.g. India is 5.5)
    private double timezone;
    // One of E (Europe), A (US/Canada), S (South America), O (Australia), Z (New Zealand), N (None) or U (Unknown)
    private char dst;

    private  AtmosphericInformation atmosphericInformation=new AtmosphericInformation();

    public AirportData() {
    }

    public AirportData(String name,String city, String country, String iata, String icao, double latitude, double longitude, double altitude, double timezone, char dst) {
        this.name=name;
        this.iata = iata;
        this.city = city;
        this.country = country;
        this.icao = icao;
        this.latitude = latitude;
        this.longitude = longitude;
        this.altitude = altitude;
        this.timezone = timezone;
        this.dst = dst;
    }

    public String getIata() {
        return iata;
    }

    public void setIata(String iata) {
        this.iata = iata;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getIcao() {
        return icao;
    }

    public void setIcao(String icao) {
        this.icao = icao;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getAltitude() {
        return altitude;
    }

    public void setAltitude(double altitude) {
        this.altitude = altitude;
    }

    public double getTimezone() {
        return timezone;
    }

    public void setTimezone(double timezone) {
        this.timezone = timezone;
    }

    public char getDst() {
        return dst;
    }

    public void setDst(char dst) {
        this.dst = dst;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public AtmosphericInformation getAtmosphericInformation() {
        return atmosphericInformation;
    }

    public void setAtmosphericInformation(AtmosphericInformation atmosphericInformation) {
        this.atmosphericInformation = atmosphericInformation;
    }

    public boolean equals(Object other) {
        if (other instanceof AirportData) {
            return ((AirportData) other).getIata().equals(this.getIata());
        }

        return false;
    }

    @Override
    public String toString() {
        return "AirportData{" +
            "name='" + name + '\'' +
            ", iata='" + iata + '\'' +
            ", city='" + city + '\'' +
            ", country='" + country + '\'' +
            ", icao='" + icao + '\'' +
            ", latitude=" + latitude +
            ", longitude=" + longitude +
            ", altitude=" + altitude +
            ", timezone=" + timezone +
            ", dst=" + dst +
            '}';
    }
}
