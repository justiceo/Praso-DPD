package DPD.Browser

import DPD.Model.DependencyType_S
import DPD.Model.Implicits._
import Models._

/**
  * Created by Justice on 3/23/2017.
  */
class DSMDataStructure_S(val dependencies: List[DependencyType_S.Value],
                         val adjMatrix: Matrix,
                         val files: List[String]) {

  val size: Int = adjMatrix.length

  //def dependents2(classId: Int): List[Int] = adjMatrix.zipWithIndex.filter((t) => t._1.exists(c => c._2 == classId)).map(t => t._2)
  def dependents(classId: Int): List[Int] = adjMatrix.zipWithIndex.collect { case t if t._1.exists(c => c._2 == classId) => t._2 }

  def dependencies(classId: Int): List[Int] = adjMatrix(classId).map(t => t._2).toList

  def subDsm(classId: Int): String = {
    val classes = (classId :: dependents(classId) ++ dependencies(classId)).distinct.sorted
    val newMatrix: Matrix = adjMatrix.zipWithIndex.collect { case t if classes.contains(t._2) => t._1 }
      .map(arr => arr.collect { case c if classes.contains(c._2) => c })
    newMatrix.map(arr => arr.map(t => t._2).toString).toString()
    flattenMatrix(expandMatrix(updateIndices(newMatrix, classes))).join("\n")
  }

  def rawString(matrix: Matrix): String = matrix.map(arr => {
    arr.map(_.toString()).join()
  }).join("\n")

  override def toString: String =
    s"${getDepLine} \n$size \n${flattenMatrix(expandMatrix).join("\n")} \n${files.join("\n")}"

  def getDepLine: String = "[" + dependencies.map(_ toString).join(",") + "]"

  def updateIndices(matrix: Matrix, originalOrder: List[Int]): Matrix =
    matrix.map(_.map((t) => (t._1, originalOrder.indexOf(t._2))))

  def expandMatrix: Matrix = expandMatrix(adjMatrix)

  def expandMatrix(matrix: Matrix): Matrix = matrix.map(arr => {
    val (el, indices) = arr.unzip
    Array.range(0, matrix.size).map(i => {
      if (indices.contains(i))
        (el(indices.indexOf(i)), i)
      else (0, i)
    })
  })

  def flattenMatrix(adjMatrix: Matrix): List[String] =
    adjMatrix.map(_.map((t) => Integer.toBinaryString(t._1)).map(s => {
      val diff = dependencies.size - s.length
      if (s.equals("0") || diff == 0) s
      else (0 until diff).map(_ => "0").reduce((a, b) => a + b) + s
    }).reduce((a, b) => a + " " + b))
}
