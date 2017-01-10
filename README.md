# weather-dist
The Airport Weather Service (AWS) is a REST application for collecting and redistributing meteorological data for a handful of airports.

## Project Background

The Airport Weather Service (AWS) is a REST application for collecting and redistributing meteorological data for a
handful of airports. The service provides two distinct interfaces. One is a query interface, used by dozens of client
systems to retrieve information such mean temperature and max wind speed. The other is a collector interface used by
airports to update meteorological data stored in AWS. You'll find more detail about these interfaces in
WeatherCollectorEndpoint.java and WeatherQueryEndpoint.java. To see the service in action, you can run the
included run-ws.sh which launches the server and demonstrates a simple client hitting the REST endpoint.

Over time, the number of airports and clients of the service has grown. Today, the application finds itself filling a
role much larger than it was initially designed for - resulting in poor reliability and slow performance. 

### REST API

The application currently supports the following REST APIs, you should not change the parameters or response format, but may change the implementation.
* GET /collect/ping
* POST /collect/weather/{iata}/{pointType}
* GET /query/ping
* GET /query/weather/{iata}/{radius}
* POST /collect/airport/{iata}/{lat}/{long}
* DELETE /collect/airport/{iata}
