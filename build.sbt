import Dependencies._
import scala.io.Source
import java.io.PrintWriter

lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      organization := "com.justiceo",
      scalaVersion := "2.12.1",
      version      := "0.1.0-SNAPSHOT"
    )),
    name := "Main",
    libraryDependencies += scalaTest % Test,    
    libraryDependencies += "com.lihaoyi" % "ammonite" % "0.8.4" cross CrossVersion.full,
    libraryDependencies += "com.lihaoyi" %% "ammonite-ops" % "0.8.4",
    libraryDependencies += "org.scala-lang" % "scala-compiler" % scalaVersion.value
  )

lazy val fixPaths = taskKey[Unit]("Fixes the file paths in DSM files")
fixPaths := {
  def fixFilePaths(dsmFilePath: String) = {    
    def fix(path: String): String = 
        if (path.endsWith("_java"))
          path.replace(".", "\\").replace("_", ".")
        else path
    val (depLine :: countLine :: matrix_files) = Source.fromFile(dsmFilePath).getLines().toList
    val count = Integer.parseInt(countLine)
    val matrix = matrix_files.take(count)
    val files = matrix_files.takeRight(count).map(fix)
    val newLines: List[String] = depLine :: (countLine :: matrix ++ files)
    new PrintWriter(dsmFilePath) {
      write(newLines.mkString("\n"))
      close()
    }
  }

  // fix dsms in Main
  (new File("src\\main\\resources\\dsm")).listFiles.filter(_.isFile).map(_.getAbsolutePath).foreach(fixFilePaths)

  // fix dsms in Test
  (new File("src\\test\\resources\\dsm")).listFiles.filter(_.isFile).map(_.getAbsolutePath).foreach(fixFilePaths)
  
}