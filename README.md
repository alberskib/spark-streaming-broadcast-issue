# spark-streaming-broadcast-issue

Minimal, Complete, and Verifiable example reproducing issue with spark broadcasted variables.

## Usage

Repository contains source code as well as input data in order to reproduce error. The only thing you need is to download spark i.e 1.3 for hadoop 2.6 or later.

After preparing spark you can follow next steps:
 * create package (`./sbt assembly` from project main dir)
 * on another terminal run `nc -lk 9000` and from time to time enter some text
 * invoke below command where :
    * CHECKPOINT_DIR is location of directory where checkpoints will be saved i.e `file:///home/user/check`
    * localhost - hostname of machine on which netcat is invoked
    * 9000 - port number on which netcat is invoked
 ```
 $SPARK_HOME bin/spark-submit --class  pl.example.spark.StreamingTest --master local[4] target/scala-2.10/spark-streaming-broadcast-issue-assembly-0.0.1-SNAPSHOT.jar CHECKPOINT_DIR localhost 9000
 ```
 * wait some time in order to enable spark to process few batches
 * rerun command starting spark processing


 As a result bext exception will be throwned:
 ```
 ClassCastException: [B cannot be cast to pl.example.spark.StreamingTestReporter
 	at pl.example.spark.StreamingTest$$anonfun$createContext$1$$anonfun$apply$2$$anonfun$apply$4$$anonfun$apply$5.apply(StreamingTest.scala:38)
 	at pl.example.spark.StreamingTest$$anonfun$createContext$1$$anonfun$apply$2$$anonfun$apply$4$$anonfun$apply$5.apply(StreamingTest.scala:36)
 	at scala.collection.immutable.List.foreach(List.scala:318)
 	at pl.example.spark.StreamingTest$$anonfun$createContext$1$$anonfun$apply$2$$anonfun$apply$4.apply(StreamingTest.scala:36)
 	at pl.example.spark.StreamingTest$$anonfun$createContext$1$$anonfun$apply$2$$anonfun$apply$4.apply(StreamingTest.scala:33)
 	at org.apache.spark.rdd.RDD$$anonfun$foreachPartition$1$$anonfun$apply$29.apply(RDD.scala:898)
 	at org.apache.spark.rdd.RDD$$anonfun$foreachPartition$1$$anonfun$apply$29.apply(RDD.scala:898)
 	at org.apache.spark.SparkContext$$anonfun$runJob$5.apply(SparkContext.scala:1848)
 	at org.apache.spark.SparkContext$$anonfun$runJob$5.apply(SparkContext.scala:1848)
 	at org.apache.spark.scheduler.ResultTask.runTask(ResultTask.scala:66)
 	at org.apache.spark.scheduler.Task.run(Task.scala:88)
 	at org.apache.spark.executor.Executor$TaskRunner.run(Executor.scala:214)
 	at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1142)
 	at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:617)
 	at java.lang.Thread.run(Thread.java:745)
 ```


