package DPD.Model

import scala.io.Source

/**
  * Created by Justice on 3/23/2017.
  */
class DSMModel_S {

  def parse(path: String): (List[String], Int, List[String], List[String]) = {
    val depLine::countLine::matrix_files = Source.fromFile(path).getLines().toList
    val count = Integer.parseInt(countLine)
    (extractArray(depLine), count, matrix_files.take(count), matrix_files.takeRight(count))
  }

  def extractArray(arg: String): List[String] =
    arg.replace("[", "").replace("]", "").split(",").map(x => x.trim).toList
}
