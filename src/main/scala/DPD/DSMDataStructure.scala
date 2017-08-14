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

  /** Returns the absolute path to the src directory */
  lazy val sourceRoot: String = files.reduce((a, b) => a.zip(b).takeWhile((t) => t._1 == t._2).map(_._1).mkString)
  
  /** Returns a list of fully qualified (fq) class names generated from file path 
   * i.e. src/main/scala/DPD/ScalaREPL.java become DPD.ScalaREPL (notice the package name is included since similar names can exist in different packages)
   */
  val types: List[String] = files.indices.map(getFQType).toList

  /** Returns the pairs of classes that satisfy all the given dependencies */
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

    val classes: List[Int] = classIds.flatMap(classId => classId :: dependents(classId) ++ dependencies(classId)).distinct.sorted.toList
    val newMatrix: Matrix = adjMatrix.zipWithIndex.collect { case t if classes.contains(t._2) => t._1 }
      .map(arr => arr.collect { case c if classes.contains(c._2) => (c._1, classes.indexOf(c._2)) })
    //newMatrix.map(arr => arr.map(t => t._2).toString).toString()
    val depIndices = getIndexUsedDependencies(newMatrix)
    new DSMDataStructure(
      dependencyTypes.zipWithIndex.collect { case t if depIndices.contains(t._2) => t._1 },
      refactorMatrix(newMatrix, depIndices),
      files.zipWithIndex.collect { case t if classes.contains(t._2) => t._1 }
    )
  }

  /** Alias to subDsm that fq class names as vargs and converts to list */
  def subDsm(classNames: String*): DSMDataStructure = subDsm(classNames.toList)
  
  /** Alias to _subDsm that takes class names as string and converts them to class ids - which _subDsm needs */
  def subDsm(classNames: List[String]): DSMDataStructure = _subDsm(resolve(classNames: _*): _*)

  /** Returns a string representation of the adjacency matrix - list of tuples (dependencyType, classIndex) */
  def matrixStr: String = adjMatrix.map(_.map(_.toString).mkString).mkString("\n")

  /** Returns a dsm file representation of the dependency, matrix and files */
  override def toString: String = {
    val depLine = dependencyTypes.map(_.toString).mkString("[", ",", "]")
    s"$depLine \n$size \n${rowsAsStrings(squarify()).mkString("\n")} \n${files.mkString("\n")}"
  }

  /** Returns a "square" version of the adjacency matrix by including the empty dependencies that were omitted */
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

  /** same as rowAsStrings, but adds spaces to 0's to achieve a square effect */
  def paddedRowsAsStrings(adjMatrix: Matrix): List[String] = {
    val singleCharPadding = (0 until dependencyTypes.size - 1).map(_ => " ").mkString("")
    val topIndex = "    " + (0 until adjMatrix.size).map(i => i + singleCharPadding).mkString(" ")
    val underLine = (0 to topIndex.length).map(i => "-").mkString("")
    val matStr = adjMatrix.map(_.map((t) => Integer.toBinaryString(t._1)).map(s => {
      val diff = dependencyTypes.size - s.length
      if (s.equals("0")) "0" + singleCharPadding
      else if (diff == 0) s
      // if the binary form is shorter than expected length, add zeros in front
      else (0 until diff).map(_ => "0").mkString("") + s
    }).reduce((a, b) => a + " " + b))
    val withSideIndex = matStr.zipWithIndex.map(s => s._2 + " | " + s._1)
    topIndex :: underLine :: withSideIndex
  }

  /** Returns a decimal that represents the binary string of this dependency */
  def toBinaryMask(dep: DependencyType.Value): Int = math.pow(2, (dependencyTypes.size - 1 - dependencyTypes.indexOf(dep)).toDouble).toInt


  /** Returns a "highly-probable" package type of the given class */
  def getFQType(classId: Int): String = files(classId).replace(sourceRoot, "")


  /** Returns n-top keyInterfaces for the given dsm alongsize the number of dependents they have (as tuple)
   * An interface is a keyInterface if it has several dependents.
   * The more dependents an interface has, the higher it's rank in the list
   * todo: refactor this code to something readable - not just something that works.
   */
  def keyInterface(top: Int = 1): List[(Int, Int)] = adjMatrix.flatten.map(_._2).groupBy(i => i).mapValues(_.size).toList.sortBy(_._2).reverse.take(top)

  /**
   * Companion function to keyInterface that translates the classIds to class names for easy identification
   * You are more likely to find this function useful
   * KeyInterface is more useful if you want to see the number of dependents.
   */
  def namedKeyInterfaces(top: Int = 1): List[String] = keyInterface(top).map(t => nice(t._1))


  /////////////////////
  /// Dependency Types aliases
  /////////////////////
  def IMPLEMENT: List[(Int, Int)] = dependencyPair(DependencyType.IMPLEMENT)

  def EXTEND: List[(Int, Int)] = dependencyPair(DependencyType.EXTEND)

  /** Returns all the extend, implement pair in the dsm
   * Given (A, B), A extends B or A implements B for all tuples in SPECIALIZE
   */
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

  def nice: String = {
    val depLine = dependencyTypes.map(_.toString).mkString("[", ",", "]")
    val numberedFiles = files.zipWithIndex.map(t => "(" + t._2 + ") " + getFQType(t._2))
    s"$depLine \n${paddedRowsAsStrings(squarify()).mkString("\n")} \n${numberedFiles.mkString("\n")}"
  }

  /** input should be the result from 'find' function
    * return the class indices of the classes specified as args
    */
  def resolve(args: String*): List[Int] = types.zipWithIndex.collect { case t if args.contains(t._1) => t._2 }

  /** Returns all the classes that either extend, or implement the given class */
  def subClasses(classId: Int): List[(Int, Int)] = SPECIALIZE.filter(t => t._2 == classId)

  /** Alias for subClasses above that takes vargs */
  def subClasses(classIds: Int*): List[(Int, Int)] = classIds.flatMap(subClasses).toList

  /** Returns a list of classes that exhibit the given dependencies in this entity */
  def classesThat(deps: List[DependencyType.Value], entity: Entity): List[(Int, Int)] =
    dependencyPair(deps: _*).filter(t => entity.ids.contains(t._2))

  def isTestClass(classId: Int): Boolean = nice(classId).endsWith("Test")

}
