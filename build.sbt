name := "TwittSearchTest"

version := "1.0"

lazy val `twittsearchtest` = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.11.8"

libraryDependencies ++= Seq( jdbc , cache , ws   , specs2 % Test )

unmanagedResourceDirectories in Test <+=  baseDirectory ( _ /"target/web/public/test" )

resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"

libraryDependencies += javaJpa

// https://mvnrepository.com/artifact/dom4j/dom4j
libraryDependencies += "dom4j" % "dom4j" % "1.6"

libraryDependencies += javaWs % "test"

// https://mvnrepository.com/artifact/org.hibernate/hibernate-core
// must exclude dom4j in hibernate core because it causes staxeventreader exceptions
// http://stackoverflow.com/questions/36222306/caused-by-java-lang-classnotfoundexception-org-dom4j-io-staxeventreader
libraryDependencies += "org.hibernate" % "hibernate-core" % "5.2.3.Final" exclude("dom4j", "dom4j") exclude("javax.transaction", "jta") exclude("org.slf4j", "slf4j-api")

libraryDependencies += "org.twitter4j"% "twitter4j-core"% "4.0.4"