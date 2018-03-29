package mx.iteso.desi.cloud.hw2;

import org.apache.hadoop.mapreduce.*;
import org.apache.hadoop.io.*;

import mx.iteso.desi.cloud.GeocodeWritable;

public class GeocodeReducer extends Reducer<Text, GeocodeWritable, Text, Text> {

  public void reduce(Text key, Iterable<GeocodeWritable> values, Context context)
      throws java.io.IOException, InterruptedException {
    boolean inCitiesRadii = false;
    Double[] coordinates = new Double[2];

    for (GeocodeWritable gw : values) {
      if (gw.getName().toString().equals("geocode")) {
        inCitiesRadii = true;
        coordinates[0] = gw.getLatitude();
        coordinates[1] = gw.getLongitude();
        break;
      }
    }

    if (inCitiesRadii) {
      String coordsString = String.format("(%f,%f)", coordinates[0], coordinates[1]);
      for (GeocodeWritable value : values) {
        if (!value.getName().toString().equals("geocode")) {
          String catURL = String.format("%s\t%s", key.toString(), value.getName().toString());
          context.write(new Text(coordsString), new Text(catURL));
        }
      }
    }
  }
}
