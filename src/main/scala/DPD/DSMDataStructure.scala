package DPD

import DPD.Types._

/**
  * Created by Justice on 3/23/2017.
  */
class DSMDataStructure(val dependencyTypes: List[DependencyType.Value],
                       val adjMatrix: Matrix,
                       val files: List[String]) {

  /** The size of the matrix, also the number of files  */
  val size: Int = adjMatrix.length

  /** The folder prefix to the source root, usually starting with C:/ */
  lazy val sourceRoot: String = files.reduce((a, b) => a.zip(b).takeWhile((t) => t._1 == t._2).map(_._1).mkString)
  val types: List[String] = files.indices.map(getFQType).toList

  /** takes a variable number of dependencyTypes and returns pair classes that satisfy them all */
  def dependencyPair(deps: DependencyType.Value*): List[(Int, Int)] = {
    val bitmask = deps.map(toBinaryMask).reduceLeft(_ | _)
    adjMatrix.trio.collect { case t if (t._1 & bitmask) == bitmask => (t._3, t._2) }
  }

  /** Returns all the classes that are dependent on this class. a.k.a column dependency */
  def dependents(classId: Int): List[Int] = adjMatrix.zipWithIndex.collect { case t if t._1.exists(c => c._2 == classId) => t._2 }

  /** Returns all the classes that have all this dependents on them
    * Each class in the result fulfills every single dependency specified
    */
  def dependents(deps: DependencyType.Value*): List[Int] = dependencyPair(deps: _*).unzip._2.distinct

  /** Returns all the classes that have the specified kind of dependency on this class */
  def dependents(classId: Int, deps: DependencyType.Value*): List[Int] = dependencyPair(deps: _*).filter(_._2 == classId).unzip._1.distinct

  /** Returns all the classes this class is dependent on. a.k.a row dependency */
  def dependencies(classId: Int): List[Int] = adjMatrix(classId).map(t => t._2).toList

  /** Returns all the classes that have all this dependencies on others */
  def dependencies(deps: DependencyType.Value*): List[Int] = dependencyPair(deps: _*).unzip._1.distinct

  /** Returns all the classes this class has the specified dependency on */
  def dependencies(classId: Int, deps: DependencyType.Value*): List[Int] = dependencyPair(deps: _*).filter(_._1 == classId).unzip._2.distinct

  /** Returns a subDsm of all classes (dependents and dependencies) related to these classes */
  def _subDsm(classIds: Int*): DSMDataStructure = {
    // adds zeros when necessary to a binary string to ensure the size is consistent
    def adjBinaryStr(n: Int): String = "00000000000" + n.toBinaryString takeRight dependencyTypes.size

    def refactorMatrix(matrix: Matrix, deps: List[Int]): Matrix =
      matrix.map(_.map((t) => {
        val newDep = adjBinaryStr(t._1).zipWithIndex.collect { case c if deps.contains(c._2) => c._1 }
        (Integer.parseInt(newDep.mkString, 2), t._2)
      }
      ))

    // given a matrix, returns the numerical indices of dependencies that are used
    def getIndexUsedDependencies(matrix: Matrix): List[Int] = {
      val used = adjBinaryStr(matrix.flatten.map((t) => t._1).reduce((a, b) => a | b))
      used.zipWithIndex.collect { case c if c._1 == '1' => c._2 }.toList
    }

    def updateIndices(matrix: Matrix, originalOrder: List[Int]): Matrix =
      matrix.map(_.map((t) => (t._1, originalOrder.indexOf(t._2))))

    val classes: List[Int] = classIds.flatMap(classId => classId :: dependents(classId) ++ dependencies(classId)).distinct.sorted.toList
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

  /** takes fq classes names, resolves their indices and calls main subdsm */
  def subDsm(classNames: List[String]): DSMDataStructure = _subDsm(resolve(classNames: _*): _*)

  /** Returns a string representation of the adjacency matrix - list of tuples (dependencyType, classIndex) */
  def matrixStr: String = adjMatrix.map(_.map(_.toString).mkString).mkString("\n")

  /** Returns a dsm file representation of the dependency, matrix and files */
  override def toString: String = {
    val depLine = dependencyTypes.map(_.toString).mkString("[", ",", "]")
    s"$depLine \n$size \n${rowsAsStrings(squarify()).mkString("\n")} \n${files.mkString("\n")}"
  }

  /** Returns a "square" adjacency matrix by including the empty dependencies that were omitted */
  def squarify(matrix: Matrix = adjMatrix): Matrix = matrix.map(arr => {
    val (el, indices) = arr.unzip
    Array.range(0, matrix.size).map(i => {
      if (indices.contains(i))
        (el(indices.indexOf(i)), i)
      else (0, i)
    })
  })

  /** Returns a list of matrix row strings - where each string is the representation of an adjacency list */
  def rowsAsStrings(adjMatrix: Matrix): List[String] =
    adjMatrix.map(_.map((t) => Integer.toBinaryString(t._1)).map(s => {
      val diff = dependencyTypes.size - s.length
      if (s.equals("0") || diff == 0) s
      else (0 until diff).map(_ => "0").mkString("") + s
    }).reduce((a, b) => a + " " + b))

  /** Returns a decimal that represents the binary string of this dependency */
  def toBinaryMask(dep: DependencyType.Value): Int = math.pow(2, (dependencyTypes.size - 1 - dependencyTypes.indexOf(dep)).toDouble).toInt


  /** Returns a "highly-probable" package type of the given class */
  def getFQType(classId: Int): String = files(classId).replace(sourceRoot, "")


  def keyInterface(top: Int = 1): List[(Int, Int)] = adjMatrix.flatten.map(_._2).groupBy(i => i).mapValues(_.size).toList.sortBy(_._2).reverse.take(top)

  def namedKeyInterfaces(top: Int = 1): List[String] = keyInterface(top).map(t => nice(t._1))


  /////////////////////
  /// Dependency Types aliases
  /////////////////////
  def IMPLEMENT: List[(Int, Int)] = dependencyPair(DependencyType.IMPLEMENT)

  def EXTEND: List[(Int, Int)] = dependencyPair(DependencyType.EXTEND)

  def SPECIALIZE: List[(Int, Int)] = (EXTEND ::: IMPLEMENT).distinct


  //////////////////////
  /// Utility functions
  //////////////////////

  /** Returns fq classes whose names contain any of the arguments to the function */
  def find(args: String*): List[String] = types.filter(f => args.exists(f.toLowerCase.contains))

  /** Returns the type (or nice name) by removing preceeding folders and the .java suffix */
  def nice(classId: Int): String = {
    val cuttoff = files(classId).lastIndexOf("\\")
    files(classId).substring(cuttoff + 1).replace(".java", "")
  }

  def nice(classIds: Int*): List[String] = classIds.map(nice).toList

  def nice(t: (Int, Int)): (String, String) = (nice(t._1), nice(t._2))

  def nice(l: List[(Int, Int)]): List[(String, String)] = l.map(nice)

  /** input should be the result from 'find' function
    * return the class indices of the classes specified as args
    */
  def resolve(args: String*): List[Int] = types.zipWithIndex.collect { case t if args.contains(t._1) => t._2 }

  def subClasses(classId: Int): List[(Int, Int)] = SPECIALIZE.filter(t => t._2 == classId)

  def subClasses(classIds: Int*): List[(Int, Int)] = classIds.flatMap(subClasses).toList

  def classesThat(deps: List[DependencyType.Value], entity: Entity): List[(Int, Int)] =
    dependencyPair(deps: _*).filter(t => entity.ids.contains(t._2))

}
