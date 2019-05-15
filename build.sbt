name := "appsflyer"

version := "0.1"

scalaVersion := "2.11.12"

libraryDependencies ++= Seq(
  "org.apache.spark" % "spark-core_2.11" % "2.4.1",
  "org.apache.spark" %% "spark-sql" % "2.4.1")

assemblyMergeStrategy in assembly := {
  case PathList("org", "aopalliance", _*) => MergeStrategy.last
  case PathList("javax", "inject", _*) => MergeStrategy.last
  case PathList("javax", "servlet", _*) => MergeStrategy.last
  case PathList("javax", "activation", _*) => MergeStrategy.last
  case PathList("org", "apache", _*) => MergeStrategy.last
  case PathList("com", "google", _*) => MergeStrategy.last
  case PathList("com", "esotericsoftware", _*) => MergeStrategy.last
  case PathList("com", "codahale", _*) => MergeStrategy.last
  case PathList("com", "yammer", _*) => MergeStrategy.last
  case "about.html" => MergeStrategy.rename
  case PathList("META-INF", _*) => MergeStrategy.discard
  case "plugin.properties" => MergeStrategy.last
  case "log4j.properties" => MergeStrategy.last
  case _ => MergeStrategy.last
}
