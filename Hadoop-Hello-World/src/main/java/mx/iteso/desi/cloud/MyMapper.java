package mx.iteso.desi.cloud;

import java.io.IOException;
import java.util.StringTokenizer;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class MyMapper extends Mapper<Object, Text, Text, IntWritable> {

  protected void map(Object key, Text value, Context context) throws IOException, InterruptedException {
    Text word = new Text();
    StringTokenizer itr = new StringTokenizer(value.toString());

    while (itr.hasMoreTokens()) {
      word.set(itr.nextToken().replaceAll("\\W", ""));
      context.write(word, new IntWritable(1));
    }
  }
}
