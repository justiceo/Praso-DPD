package DPD

import java.io.PrintWriter
import scala.tools.nsc.Settings
import scala.tools.nsc.interpreter._
import DPD.Implicits._
import Models._
//import ammonite._

import scala.io.Source

/**
  * Created by Justice on 3/23/2017.
  */
object Main {
  val testDsmFile = "src\\main\\resources\\dsm\\head-first-design-patterns.dsm"

  def main(args: Array[String]): Unit = {
    println("Welcome to Praso-DPD")
    val (dependencies, count, matrix, files) = parse(testDsmFile)
    val dsmDs = new DSMDataStructure(dependencies, matrix, files)
    val subDsmDs = dsmDs.subDsm(1)
    println(dsmDs.namedKeyInterfaces(5))
    println(subDsmDs.matrixStr)

  }

  def parse(path: String): (List[DependencyType.Value], Int, Matrix, List[String]) = {
    val depLine :: countLine :: matrix_files = Source.fromFile(path).getLines().toList
    val count = Integer.parseInt(countLine)

    def extractDepArray(arg: String): List[DependencyType.Value] =
      arg.replace("[", "").replace("]", "").split(",").map(x => DependencyType.withName(x.trim.toUpperCase)).toList

    def extractDepMatrix(lines: List[String]): List[Array[(Int, Int)]] =
      lines.map(l => l.split(" ").map(c => Integer.parseInt(c, 2)).zipWithIndex.filter(t => t._1 != 0))

    (extractDepArray(depLine), // dependency line
      count, // size of the matrix
      extractDepMatrix(matrix_files.take(count)), // the matrix parsed
      matrix_files.takeRight(count)) // the file paths fixed
  }

  def export(dsm: DSMDataStructure, filepath: String = "export.dsm"): Boolean = {
    new PrintWriter(filepath) {
      write(dsm.toString)
      close()
    }
    true
  }
}
