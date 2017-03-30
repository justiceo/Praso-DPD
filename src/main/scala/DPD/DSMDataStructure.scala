package DPD

import DPD.Implicits._
import Models._

/**
  * Created by Justice on 3/23/2017.
  */
class DSMDataStructure(val dependencyTypes: List[DependencyType.Value],
                       val adjMatrix: Matrix,
                       val files: List[String]) {

  lazy val size: Int = adjMatrix.length
  lazy val sourceRoot: String = files.reduce((a, b) => a.zip(b).takeWhile((t) => t._1 == t._2).map(_._1).mkString)

  //def dependents2(classId: Int): List[Int] = adjMatrix.zipWithIndex.filter((t) => t._1.exists(c => c._2 == classId)).map(t => t._2)
  def dependents(classId: Int): List[Int] = adjMatrix.zipWithIndex.collect { case t if t._1.exists(c => c._2 == classId) => t._2 }

  def dependencies(classId: Int): List[Int] = adjMatrix(classId).map(t => t._2).toList

  def subDsm(classId: Int): DSMDataStructure = {
    def refactorMatrix(matrix: Matrix, deps: List[Int]): Matrix =
      matrix.map(_.map((t) => {
        val newDep = t._1.toBinaryString.zipWithIndex.collect { case c if deps.contains(c._2) => c._1 }
        (Integer.parseInt(newDep.mkString), t._2)
      }
      ))

    def getIndexUsedDependencies(matrix: Matrix): List[Int] = {
      val used = Integer.toBinaryString(matrix.flatten.map((t) => t._1).reduce((a, b) => a | b))
      used.zipWithIndex.collect { case c if c._1 == '1' => c._2 }.toList
    }

    def updateIndices(matrix: Matrix, originalOrder: List[Int]): Matrix =
      matrix.map(_.map((t) => (t._1, originalOrder.indexOf(t._2))))

    val classes = (classId :: dependents(classId) ++ dependencies(classId)).distinct.sorted
    val newMatrix: Matrix = adjMatrix.zipWithIndex.collect { case t if classes.contains(t._2) => t._1 }
      .map(arr => arr.collect { case c if classes.contains(c._2) => c })
    //newMatrix.map(arr => arr.map(t => t._2).toString).toString()
    val depIndices = getIndexUsedDependencies(newMatrix)
    new DSMDataStructure(
      dependencyTypes.zipWithIndex.collect { case t if depIndices.contains(t._2) => t._1 },
      refactorMatrix(newMatrix, depIndices),
      files.zipWithIndex.collect { case t if classes.contains(t._2) => t._1 }
    )
  }

  def matrixStr(matrix: Matrix = adjMatrix): String = matrix.map(_.map(_.toString).mkString).mkString("\n")

  override def toString: String = {
    val depLine = dependencyTypes.map(_ toString).mkString("[", ",", "]")
    s"$depLine \n$size \n${flattenMatrix(expandMatrix()).mkString("\n")} \n${files.mkString("\n")}"
  }

  def expandMatrix(matrix: Matrix = adjMatrix): Matrix = matrix.map(arr => {
    val (el, indices) = arr.unzip
    Array.range(0, matrix.size).map(i => {
      if (indices.contains(i))
        (el(indices.indexOf(i)), i)
      else (0, i)
    })
  })

  def flattenMatrix(adjMatrix: Matrix): List[String] =
    adjMatrix.map(_.map((t) => Integer.toBinaryString(t._1)).map(s => {
      val diff = dependencyTypes.size - s.length
      if (s.equals("0") || diff == 0) s
      else (0 until diff).map(_ => "0").mkString("") + s
    }).reduce((a, b) => a + " " + b))

  def toBinaryMask(dep: DependencyType.Value): Int = math.pow(2, dependencyTypes.size - 1 - dependencyTypes.indexOf(dep)).toInt

  def getType(classId: Int): String = {
    val cuttoff = files(classId).lastIndexOf("\\")
    files(classId).substring(cuttoff+1).replace(".java", "")
  }

  def getFQType(classId: Int): String = files(classId).replace(sourceRoot, "")

  def findTypes(types: String*): List[String] =
    files.filter(f => types.exists(f.contains))

  def keyInterface(top: Int = 1): List[(Int, Int)] = adjMatrix.flatten.map(_._2).groupBy(i => i).mapValues(_.size).toList.sortBy(_._2).reverse.take(top)

  def namedKeyInterfaces(top: Int = 1): List[String] = keyInterface(top).map(t => getType(t._1))

  def dependencyPair(deps: DependencyType.Value*): List[(Int, Int)] = {
    val bitmask = deps.map(toBinaryMask).reduceLeft(_ | _)
    adjMatrix.trio.collect { case t if (t._1 & bitmask) == bitmask => (t._3, t._2) }
  }
}
