package mx.iteso.desi.cloud.hw2;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import mx.iteso.desi.cloud.Geocode;
import mx.iteso.desi.cloud.GeocodeWritable;
import mx.iteso.desi.cloud.ParseTriple;
import mx.iteso.desi.cloud.ParserCoordinates;
import mx.iteso.desi.cloud.Triple;

import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.*;

public class GeocodeMapper extends Mapper<LongWritable, Text, Text, GeocodeWritable> {

  private static class City {
    public String name;
    public Double lat;
    public Double lon;

    public City(String name, Double lat, Double lon) {
      this.name = name;
      this.lat = lat;
      this.lon = lon;
    }
  }

  public static List<City> cities;

  static {
    cities = new ArrayList<>();
    cities.add(new City("Philadelphia", 39.88, -75.25));
    cities.add(new City("Houston", 29.97, -95.35));
    cities.add(new City("Seattle", 47.45, -122.30));
    cities.add(new City("Guadalajara", 20.66, 103.39));
    cities.add(new City("Monterrey", 25.67, -100.31));
  }

  protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
    Triple triple = ParseTriple.parseTriple(value.toString());
    if (triple != null) {
      Text text = new Text(triple.getSubject());
      GeocodeWritable geocodeWritable;
  
      if (triple.getRelationship().equals("http://xmlns.com/foaf/0.1/depiction")) {
        // Is image
        Geocode geocode = new Geocode(triple.getObject(), 0l, 0l);
        geocodeWritable = new GeocodeWritable(geocode);
      } else if (triple.getRelationship().equals("http://www.georss.org/georss/point")) {
        // Is Geocode
        Double[] coordinates = ParserCoordinates.parseCoordinates(triple.getObject());
        if (coordinates == null) {
          return;
        }
        Geocode geocode = new Geocode("geocode", coordinates[0], coordinates[1]);
        if (!inCitiesRadii(geocode)) {
          return;
        }
        geocodeWritable = new GeocodeWritable(geocode);
      } else {
        return;
      }
      context.write(text, geocodeWritable);
    }
  }

  private static boolean inCitiesRadii(Geocode g) {
    return cities.stream().anyMatch(c -> g.getHaversineDistance(c.lat, c.lon) <= 5000);
    // for (City c : cities) {
    //   double distInMeters = g.getHaversineDistance(c.lat, c.lon);
    //   if(distInMeters <= 5000)
    //     return true;
    // }
    // return false;
  }
}
