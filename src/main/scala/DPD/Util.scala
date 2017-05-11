package DPD

import java.io.PrintWriter
import scala.io.Source

/**
  * Created by Justice on 3/23/2017.
  */
object Util {
  val testDsmFile = "dsm/simpleVisitorPattern.dsm"

  var pocketCounter:Int = 10000;
  def nextPocket:Int = {
    pocketCounter += 1
    pocketCounter
  }
  
  def resource(file: String): String = getClass.getClassLoader.getResource(file).getPath

  def parse: DSMDataStructure = parse(testDsmFile)

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
