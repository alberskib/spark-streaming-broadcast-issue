package pl.example.spark

import java.io.FileWriter
import java.util.concurrent.Executors


import org.apache.commons.lang.RandomStringUtils

import scala.concurrent.ExecutionContext
import scala.util.Try
import scala.util.control.NonFatal


class FileSender(name: String){
  private var out : FileWriter = null
  def connect() = {
    out = new FileWriter(name)
  }

  def isConnected() = {
    out != null
  }

  def send(name: String, value: String, timestamp: Long) = {
    out.write(s"$name: $value : $timestamp\n")
  }

  def flush() = {
    out.flush()
  }

  def close() = {
    out.close()
  }
}

trait TestReporter {
  def send(name: String, value: String, timestamp: Long) : Unit
  def flush() : Unit
}

class StreamingTestReporter(prefix: String, batchSize: Int) extends TestReporter with Serializable{

  var counter = 0

  @transient
  private lazy val sender : FileSender = initialize()

  @transient
  private lazy val threadPool = ExecutionContext.fromExecutorService(Executors.newSingleThreadExecutor())

  private def initialize() = {
    val sender = new FileSender(
      RandomStringUtils.randomAlphanumeric(15)
    )
    sys.addShutdownHook{
      sender.close()
    }
    sender
  }

  override def send(name: String, value: String, timestamp: Long) : Unit = {
    threadPool.submit(new Runnable {
      override def run(): Unit = {
        try {
          counter += 1
          if (!sender.isConnected)
            sender.connect()
          sender.send(s"$prefix.$name", value, timestamp)
          if (counter % batchSize == 0)
            sender.flush()
        }catch {
          case NonFatal(e) => {
            println(s"Problem with sending metric to graphite $prefix.$name: $value at $timestamp", e)
            Try{sender.close()}.recover{
              case NonFatal(e) => println("Error closing graphite", e)
            }
          }
        }
      }
    })
  }

  override def flush(): Unit = {
    threadPool.submit(new Runnable {
      override def run(): Unit = {
        sender.flush()
      }
    })
  }
}



