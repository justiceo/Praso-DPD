package DPD

import Models.Matrix
import org.scalatest._
import scala.io.Source

class DSMDataStructureSpec extends FlatSpec with Matchers {

    lazy val testDsmFile = "src\\test\\resources\\test.dsm"
    lazy val (dependencies, count, adjMatrix, files) = Main.parse(testDsmFile)
    lazy val dsmDS = new DSMDataStructure(dependencies, adjMatrix, files)

    "toString" should "return original dsm file input" in {
        val dsmStr = dsmDS.toString.split("\n").toList.map(_.trim.toUpperCase)
        val original = Source.fromFile(testDsmFile).getLines().toList.map(_.trim.toUpperCase)
        dsmStr shouldEqual original
    }
}