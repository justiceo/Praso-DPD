package DPD.Browser

import DPD.Model.DependencyType
import DPD.Util
import DPD.Util._

/**
  * Created by I853985 on 3/9/2017.
  */
class DSMData (matrix: Array[String], filePaths: Array[String], dependencies: List[DependencyType]) {

  // constructor
  if (matrix.length != filePaths.length)
    throw new IllegalArgumentException("matrix size must equal number of files")
  val matrixSize = matrix.length
  val dependencyTypes = Map[DependencyType, Integer]()
  val allClasses: List[ClassNode] = List.empty
  val typeDict: Map[String, Int] = Map.empty

  // associate the dependies with an index for faster quering
  for(d <- 0 until dependencies.size)
    dependencyTypes + (dependencies(d) -> d)

  // initialize all nodes and typeDict
  for(c <- 0 until matrixSize) {
    typeDict + (Util.getType(filePaths(c)) -> c)
    allClasses :+ new ClassNode()
  }

  // connect the dots
  for(c <- 0 until matrixSize) {
    val cells: Array[String] = matrix(c).trim.split(" ")
    cells.zipWithIndex.foreach {
      case (a,_) if invalidCell(a) =>
        throw new IllegalStateException(s"Invalid cell: $a, # of deps: ${dependencies.size}")
      case (a,i) if !emptyCell(a) => {
          val dn: DepNode = new DepNode(a, c, i)
          allClasses(c).row :+ dn
          allClasses(c).column :+ dn
      }
    }
  }

  def invalidCell(cell: String) = cell != "0" && cell.length != dependencies.size
  def emptyCell(cell: String) = cell == "0"

  class ClassNode(
                   val column: List[DepNode] = List.empty,
                   val row: List[DepNode] = List.empty,
                   val filePath: String = "") {}

  class DepNode (value: String, row: Int, col: Int) {
    val numValue = Integer.parseInt(value);
    override def toString(): String = s"($row, $col, $value)"
  }

}
