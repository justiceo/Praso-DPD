package DPD

import java.io.PrintWriter

import DPD.Browser.DSMDataStructure_S
import DPD.DependencyType_S
import DPD.Implicits._
import Models._

import scala.io.Source

/**
  * Created by Justice on 3/23/2017.
  */
object Main_S {
  val testDsmFile = "resources\\dsm\\simpleObserverPattern.dsm"

  def main(args: Array[String]): Unit = {
    val (dependencies, count, adjMatrix, files) = parse(testDsmFile)

    val dsm = new DSMDataStructure_S(dependencies, adjMatrix, files)
    println("dependendies", dependencies)
    println(dsm.rawString())
    println(dsm.adjMatrix.trio)
  }

  def parse(path: String): (List[DependencyType_S.Value], Int, Matrix, List[String]) = {
    val depLine :: countLine :: matrix_files = Source.fromFile(path).getLines().toList
    val count = Integer.parseInt(countLine)

    def extractDepArray(arg: String): List[DependencyType_S.Value] =
      arg.replace("[", "").replace("]", "").split(",").map(x => DependencyType_S.withName(x.trim.toUpperCase)).toList

    def extractDepMatrix(lines: List[String]): List[Array[(Int, Int)]] =
      lines.map(l => l.split(" ").map(c => Integer.parseInt(c, 2)).zipWithIndex.filter(t => t._1 != 0))

    def fixFilePath(paths: List[String]): List[String] =
      paths.map(l => {
        if (l.endsWith("_java"))
          l.replace(".", "\\").replace("_", ".")
        else l
      })


    (extractDepArray(depLine), // dependency line
      count, // size of the matrix
      extractDepMatrix(matrix_files.take(count)), // the matrix parsed
      fixFilePath(matrix_files.takeRight(count))) // the file paths fixed
  }

  def export(dsm: DSMDataStructure_S, filepath: String = "export.dsm"): Boolean = {
    new PrintWriter(filepath) {
      write(dsm.toString)
      close()
    }
    true
  }
}
