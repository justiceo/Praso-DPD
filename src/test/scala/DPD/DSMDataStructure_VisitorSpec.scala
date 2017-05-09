package DPD

import Models.Matrix
import org.scalatest._
import scala.io.Source

class DSMDataStructure_VisitorSpec extends FlatSpec with Matchers {

    lazy val testDsmFile = "src\\test\\resources\\dsm\\visitor-test.dsm"
    lazy val (dependencies, count, adjMatrix, files) = Util.parse(testDsmFile)
    lazy val dsmDS = new DSMDataStructure(dependencies, adjMatrix, files)
    
    "DSMDataStructure_Visitor" should "have equal matrix and file sizes" in {
        dsmDS.adjMatrix.size shouldEqual dsmDS.files.size
    }

    "keyInterface" should "return the keyInterface with the next inorder interface when there is no mode" in {
        dsmDS.keyInterface(4) shouldEqual List((4,6), (1,5),(3,3),(7,3))
    }

    "toString" should "return original dsm file input (ignoring cases)" in {
        val dsmStr = dsmDS.toString.split("\n").toList.map(_.trim.toUpperCase)
        val original = Source.fromFile(testDsmFile).getLines().toList.map(_.trim.toUpperCase)
        dsmStr shouldEqual original
    }
    
}