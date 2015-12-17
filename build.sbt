import sbt._
import sbt.Keys._

lazy val commonSettings = Seq(
  organization := "pl.example.spark",
  name := "spark-streaming-broadcast-issue",
  version := "0.0.1-SNAPSHOT",
  scalacOptions in Compile ++= Seq(
    "-encoding", "UTF-8",
    "-target:jvm-1.7",
    "-deprecation",
    "-feature",
    "-unchecked",
    "-Xlog-reflective-calls",
    "-Xlint"),
  assemblyMergeStrategy in assembly := {
    case PathList(xs@_*) if xs.last == "pom.xml" || xs.last == "pom.properties" =>
      MergeStrategy.rename
    case PathList("META-INF", xs@_*) =>
      (xs map {
        _.toLowerCase
      }) match {
        case ("manifest.mf" :: Nil) | ("index.list" :: Nil) | ("dependencies" :: Nil) =>
          MergeStrategy.discard
        case ps@(x :: xs) if ps.last.endsWith(".sf") || ps.last.endsWith(".dsa") =>
          MergeStrategy.discard
        case "plexus" :: xs =>
          MergeStrategy.discard
        case "services" :: xs =>
          MergeStrategy.filterDistinctLines
        case ("spring.schemas" :: Nil) | ("spring.handlers" :: Nil) =>
          MergeStrategy.filterDistinctLines
        case _ => MergeStrategy.discard
      }
    case x => MergeStrategy.last
  }
)

scalaVersion := "2.10.5"

lazy val root = (project in file("."))
  .settings(commonSettings : _*)

resolvers +=
  "Apache snapshots" at "http://repository.apache.org/snapshots/"

libraryDependencies ++= Seq(
  "net.sf.supercsv" % "super-csv" % "2.2.0",
  "org.scalatest" %% "scalatest" % "2.2.5" % "test",
  "org.apache.spark" %% "spark-streaming" % "1.5.1" % "provided",
  "org.apache.spark" %% "spark-core" % "1.5.1" % "provided",
  "org.apache.spark" %% "spark-sql" % "1.5.1" % "provided"
)
