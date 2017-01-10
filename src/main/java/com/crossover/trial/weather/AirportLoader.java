package com.crossover.trial.weather;

import com.crossover.trial.weather.model.AirportData;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

/**
 * A simple airport loader which reads a file from disk and sends entries to the webservice
 * <p>
 * TODO: Implement the Airport Loader
 *
 * @author code test administrator
 */
public class AirportLoader {

    private static final String BASE_URI = "http://localhost:9090";


    final static RestTemplate restTemplate = new RestTemplate();


    public static void main(String args[]) throws IOException {

        //args=new String[]{"airports.dat"};
        if (args.length < 1) {
            System.err.println("Please provide us with path of airportDataFile");
            System.exit(1);
        }
        File airportDataFile = new File(args[0]);
        if (!airportDataFile.exists() || airportDataFile.length() == 0) {
            System.err.println(airportDataFile + " is not a valid input");
            System.exit(1);
        }

        AirportLoader al = new AirportLoader();
        try (Stream<String> stream = Files.lines(Paths.get(airportDataFile.getAbsolutePath()))) {
            upload(stream);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static void upload(Stream<String> stream) {
        stream.map(e -> e.split(",")).map(s -> new AirportData(s[1], s[2], s[3], s[4], s[5], Double.parseDouble(s[6]), Double.parseDouble(s[7]), Double.parseDouble(s[8]), Double.parseDouble(s[9]), s[10].charAt(0))).forEach(a -> restTemplate.postForObject(BASE_URI + "/collect/airport", a, Void.class));
    }
}
