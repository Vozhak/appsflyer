package me.glowacki

import java.io.File

import org.apache.commons.io.FileUtils
import org.apache.spark.sql.SparkSession
import org.joda.time.DateTime

import scala.collection.JavaConverters._
import scala.util.Try

case class Entry(userId: String,
                 timestamp: String,
                 musicbrainzArtistId: String,
                 artist: String,
                 musicbrainzTrackId: Option[String],
                 track: String)

case class NoDatasetPathException(message: String) extends Exception(message)

object Main {

  type Session = Seq[Entry]

  def main(args: Array[String]): Unit = {

    val datasetPath: String = try {
      args.head
    } catch {
      case _: Throwable => throw NoDatasetPathException("No path to dataset was provided")
    }

    val localOutputPath = args.drop(1).lastOption.getOrElse("output.csv")

    val spark = SparkSession
      .builder()
      .appName("Appsflyer")
      .getOrCreate()

    val dataFrame = spark
      .read
      .option("delimiter", "\t")
      .csv(datasetPath)

    import spark.implicits._

    val result = spark.sparkContext.parallelize(dataFrame
      .map { row =>
        Entry(
          row.getString(0),
          row.getString(1),
          row.getString(2),
          row.getString(3),
          Try(row.getString(4)).toOption,
          row.getString(5)
        )
      }
      .rdd
      .groupBy(x => x.userId)
      .flatMap {
        case (_, group) =>
          sessions(group.toSeq.sortBy(x => DateTime.parse(x.timestamp).toDate))
      }
      .sortBy(_.length, ascending = false)
      .take(50))
      .flatMap(x => x)
      .map(entry => (entryIdentity(entry), 1))
      .reduceByKey(_ + _)
      .sortBy(_._2, ascending = false)
      .map(_._1)
      .take(10)

    FileUtils.writeLines(new File(localOutputPath), result.toSeq.asJava)

  }

  def entryIdentity(entry: Entry) = s"${entry.artist} - ${entry.track}"

  def sessions(data: Session): Seq[Session] = {
    data.sliding(2).takeWhile { x =>
      x.lastOption.forall(inSession(x.head, _))
    }.flatten.toList.distinct match {
      case x if x.isEmpty && data.nonEmpty => data.take(1) +: sessions(data.drop(1))
      case x if x.isEmpty => Seq()
      case result => result +: sessions(data.drop(result.size))
    }
  }

  def inSession(last: Entry, next: Entry): Boolean = {
    last.timestamp.plusMinutes(20).isAfter(next.timestamp)
  }

  implicit def dateToDateTime(date: String): DateTime = {
    DateTime.parse(date)
  }
}

