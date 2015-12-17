package pl.example.spark

import org.apache.spark.{SparkContext, SparkConf}
import org.apache.spark.streaming.{Seconds, StreamingContext}

object StreamingTest {
  def main(args: Array[String]): Unit = {
    val checkpointDir = args(0)
    val hostname = args(1)
    val port = args(2).toInt
    val ssc = StreamingContext.getOrCreate(checkpointDir, createContext(checkpointDir, hostname, port))

    ssc.start()
    ssc.awaitTermination()
  }

  def createContext(checkpointDir : String, hostname: String, port: Int) = () => {
      val sparkConf = new SparkConf()
        .setAppName("TestStreaming")
        .set("spark.cleaner.ttl", "86400")
        .set("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
      val sc = new SparkContext(sparkConf)
      val ssc = new StreamingContext(sc, Seconds(15))
      ssc.checkpoint(checkpointDir)
    val broadcasted = ssc.sparkContext.broadcast(new StreamingTestReporter("test", 5))

    val lines = ssc.socketTextStream(hostname, port)
    val words = lines.flatMap(_.split(" "))
    words.foreachRDD((rdd, time) =>
      rdd
        .map(x => (x,1))
        .reduceByKey(_ + _)
        .foreachPartition { partition =>

        val list = partition.toList
        list.foreach{case (value, count) =>
          println(s"$value : $count")
          broadcasted.value.send(value, count.toString, time.milliseconds/1000)
        }
        val result = list.foldLeft(0)((x,y) => x + y._2)
        println(s"Total count of partition $result")
      }
    )
      ssc
  }
}
