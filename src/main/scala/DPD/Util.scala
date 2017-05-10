package DPD

import java.io.PrintWriter
import scala.io.Source

/**
  * Created by Justice on 3/23/2017.
  */
object Util {

  type Matrix = List[Array[(Int, Int)]]
  case class CNode(classId: Int, pocket: Int, score: Int)
  type Entity = List[CNode]

  implicit class _Matrix(matrix: Matrix) {
    // converts List[Array[(data, colIndex)]] to List[(data, colIndex, rowIndex)]
    lazy val trio: List[(Int, Int, Int)] = matrix.zipWithIndex.flatMap((t) => t._1.map(d => (d._1, d._2, t._2)))
  }

  implicit class _Entity(entity: Entity) {
    def exclude(l: Entity*): Entity = ???
    def that(l: List[DependencyType.Value], e:Entity): Entity = ???
    def thatIs(l: List[DependencyType.Value]): Entity = ???
    def reconcile(a:Entity, b:Entity): (Entity, Entity) = ???
    def subClasses(a:Entity): Entity = ???
    def superClasses(a:Entity): Entity = ???
  }

  implicit class _TupleList(t: List[(Int, Int)]) {
      def asEntities: (Entity, Entity) = ???
      def min(n:Int): List[(Int, Int)] = ???
  }

  val testDsmFile = "dsm/head-first-design-patterns.dsm"
  
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

  def newEntity: Entity = List()

  def export(dsm: DSMDataStructure, filepath: String = "export.dsm"): Boolean = {
    new PrintWriter(filepath) {
      write(dsm.toString)
      close()
    }
    true
  }
}
