package mx.iteso.desi.cloud.hw2;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import mx.iteso.desi.cloud.GeocodeWritable;

public class GeocodeDriver {

  public static void main(String[] args) throws Exception {
    if (args.length != 2) {
      System.err.println("Usage: GeocodeDriver <input path> <output path>");
      System.exit(-1);
    }

    Configuration conf = new Configuration();
    Job job = Job.getInstance(conf, "GeocodeMapReduce");
    job.setJarByClass(GeocodeDriver.class);
    job.setMapperClass(GeocodeMapper.class);
    job.setReducerClass(GeocodeReducer.class);
    job.setMapOutputKeyClass(Text.class);
    job.setMapOutputValueClass(GeocodeWritable.class);
    job.setOutputKeyClass(Text.class);
    job.setOutputValueClass(Text.class);

    FileInputFormat.addInputPath(job, new Path(args[0]));
    FileOutputFormat.setOutputPath(job, new Path(args[1]));

    System.exit(job.waitForCompletion(true) ? 0 : 1);

  }
}
