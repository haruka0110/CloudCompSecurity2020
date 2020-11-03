import java.io.IOException;
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
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class JavaLogAnalysis {
    public static class LogMapper
            extends Mapper<Object, Text, Text, IntWritable>{

        private final static IntWritable plugOne  = new IntWritable(1);
        private Text word = new Text();

        private static String ConvertMonth(String s) {
            Date date = new SimpleDateFormat("MMM", Locale.ENGLISH).parse(s, new ParsePosition(0));
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            System.out.println(String.format("%02d", cal.get(Calendar.MONTH)));
            
            return String.format("%02d", cal.get(Calendar.MONTH) + 1);
        }

        @Override
        public void map(Object key, Text value, Context context
        ) throws IOException, InterruptedException {
            StringTokenizer st = new StringTokenizer(value.toString());
            while (st.hasMoreTokens()) {
                String s = st.nextToken();
                if (s.charAt(0) == '['){
                    String date = s.substring(1, s.length()-9);
                    String hour = s.substring(13, s.length()-6);
                    String[] dateSplit = date.split("/");
                    
                    dateSplit[1] = LogMapper.ConvertMonth(dateSplit[1]);
                    
                    String timestamp = dateSplit[2] + "-" + dateSplit[1] + "-" + dateSplit[0];
                    timestamp += " T " + hour + ":00:00.000";
                
                    word.set(timestamp);
                    context.write(word, plugOne);
                }
            }
        }
    }

    public static class LogReducer
            extends Reducer<Text,IntWritable,Text,IntWritable> {

        private IntWritable result = new IntWritable();

        @Override
        public void reduce(Text key, Iterable<IntWritable> values,
                           Context context
        ) throws IOException, InterruptedException {
            int reduceSum = 0;
            for (IntWritable val : values) {
                reduceSum += val.get();
            }
            result.set(reduceSum);
            context.write(key, result);
        }
    }

    public static void main(String[] args) throws Exception {
        Configuration config = new Configuration();
        Job job = Job.getInstance(config, "hadoop word count example");
        job.setJarByClass(JavaLogAnalysis.class);
        job.setReducerClass(LogReducer.class);
        job.setMapperClass(LogMapper.class);
        job.setCombinerClass(LogReducer.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);
        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));
        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}