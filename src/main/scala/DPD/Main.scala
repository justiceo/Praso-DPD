package DPD

import java.io.PrintWriter
import scala.tools.nsc.Settings
import scala.tools.nsc.interpreter._

import scala.io.Source

/**
  * Created by Justice on 3/23/2017.
  */
object Main {

  type Matrix = List[Array[(Int, Int)]]
  implicit class _Matrix(matrix: Matrix) {
    // converts List[Array[(data, colIndex)]] to List[(data, colIndex, rowIndex)]
    lazy val trio: List[(Int, Int, Int)] = matrix.zipWithIndex.flatMap((t) => t._1.map(d => (d._1, d._2, t._2)))
  }

  val testDsmFile = "dsm/head-first-design-patterns.dsm"

  def main(args: Array[String]): Unit = {
    println("Welcome to Praso-DPD")
    val dsmDs = parse(testDsmFile)
    val subDsmDs = dsmDs.subDsm(1)
    println(dsmDs.namedKeyInterfaces(5))
    println(subDsmDs.matrixStr)

  }
  
  def resource(file: String): String = getClass.getClassLoader.getResource(file).getPath

  def parse(path: String): DSMDataStructure = _parse(resource(path))

  /** takes a dsm file as input and returns a DSM data structure */
  def _parse(path: String): DSMDataStructure = {
    val depLine :: countLine :: matrix_files = Source.fromFile(path).getLines().toList
    val count = Integer.parseInt(countLine)

    def extractDepArray(arg: String): List[DependencyType.Value] =
      arg.replace("[", "").replace("]", "").split(",").map(x => DependencyType.withName(x.trim.toUpperCase)).toList

    def extractDepMatrix(lines: List[String]): List[Array[(Int, Int)]] =
      lines.map(l => l.split(" ").map(c => Integer.parseInt(c, 2)).zipWithIndex.filter(t => t._1 != 0))

    new DSMDataStructure(extractDepArray(depLine), // dependency line
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
