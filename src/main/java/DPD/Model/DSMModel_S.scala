package DPD.Model

import scala.io.Source

/**
  * Created by Justice on 3/23/2017.
  */
class DSMModel_S {

  /*
    reads a dsm file and returns content as the tuple
    (List[String] - dependency types used in dsm,
      Int - number of files (or matrix size),
      List[String] - the dsm matrix lines,
      List[String] - the corresponding file paths of the matrix lines
      )
   */
  def parse(path: String): (List[DependencyType_S.Value], Int, List[String], List[String]) = {
    val depLine::countLine::matrix_files = Source.fromFile(path).getLines().toList
    val count = Integer.parseInt(countLine)
    (extractArray(depLine).map(DependencyType_S.withName), count, matrix_files.take(count), matrix_files.takeRight(count))
  }

  def extractArray(arg: String): List[String] =
    arg.replace("[", "").replace("]", "").split(",").map(x => x.trim).toList
}
