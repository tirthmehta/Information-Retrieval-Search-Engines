mport java.io.IOException;
import java.util.*;
import java.util.StringTokenizer;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
public class InvertedIndexJob {
  public static class TokenizerMapper
       extends Mapper<Object, Text, Text, Text>{
    private static Text one =  new Text();
    private Text word = new Text();
    public void map(Object key, Text value, Context context
                    ) throws IOException, InterruptedException {
      String arr[] = value.toString().split("\t");
      one.set(arr[0]);
      StringTokenizer itr = new StringTokenizer(value.toString());
      while (itr.hasMoreTokens()) {
        word.set(itr.nextToken());
        context.write(word, one);
      }
    }
  }
  public static class IntSumReducer
       extends Reducer<Text,Text,Text,Text> {
    public void reduce(Text key, Iterable<Text> values,
                       Context context
                       ) throws IOException, InterruptedException {
        HashMap<String,Integer>     hmap = new HashMap<>();
        for(Text val: values)
        {
                if(!hmap.containsKey(val.toString()))
                {
                        hmap.put(val.toString(),1);
                }
                else
                {
                        int count = hmap.get(val.toString());
                        hmap.put(val.toString(),count+1);
                }
        }
        String res="";
        StringBuilder ans = new StringBuilder("");
        for(String x:hmap.keySet())
        {
                if(!x.contains(" "))
                {
                res= x+":"+hmap.get(x)+"\t";
                ans.append(res);
                }
        }
        Text f = new Text();
        f.set(ans.toString());
        context.write(key, f);
    }
  }
  public static void main(String[] args) throws Exception {
    Configuration conf = new Configuration();
    Job job = Job.getInstance(conf, "word count");
    job.setJarByClass(InvertedIndexJob.class);
    job.setMapperClass(TokenizerMapper.class);
    job.setReducerClass(IntSumReducer.class);
    job.setOutputKeyClass(Text.class);
    job.setOutputValueClass(Text.class);
    FileInputFormat.addInputPath(job, new Path(args[0]));
    FileOutputFormat.setOutputPath(job, new Path(args[1]));
    System.exit(job.waitForCompletion(true) ? 0 : 1);
  }
}