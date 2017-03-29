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
    libraryDependencies += scalaTest % Test
  )

lazy val fixPaths = taskKey[Unit]("Fixes the file paths in DSM files")
fixPaths := {
  def fixFilePaths(dsmFilePath: String): List[String] = {    
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
    newLines
  }  

  // fix dsms in Test
  (new File("src\\test\\resources\\")).listFiles.filter(_.isFile).map(_.getAbsolutePath).foreach(fixFilePaths)
  //println(x)
  //x.map(_.getAbsolutePath).foreach(f => { println(f); fixFilePaths(f)})

}