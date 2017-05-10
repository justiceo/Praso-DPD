package DPD

import Util.Matrix
import org.scalatest._
import scala.io.Source

class DSMDataStructure_VisitorSpec extends FlatSpec with Matchers {
    lazy val testDsmFile = "dsm/visitor-test.dsm"
    val dsm = Util.parse(testDsmFile)
    val (dependencies, count, adjMatrix, files)  = (dsm.dependencyTypes, dsm.files.size, dsm.adjMatrix, dsm.files)
    lazy val dsmDS = new DSMDataStructure(dependencies, adjMatrix, files)
    
    "DSMDataStructure_Visitor" should "have equal matrix and file sizes" in {
        dsmDS.adjMatrix.size shouldEqual dsmDS.files.size
    }

    "keyInterface" should "return the keyInterface with the next inorder interface when there is no mode" in {
        dsmDS.keyInterface(4) shouldEqual List((4,6), (1,5),(3,3),(7,3))
    }

    "toString" should "return original dsm file input (ignoring cases)" in {
        val dsmStr = dsmDS.toString.split("\n").toList.map(_.trim.toUpperCase)
        val original = Source.fromFile(Util.resource(testDsmFile)).getLines().toList.map(_.trim.toUpperCase)
        dsmStr shouldEqual original
    }
    
}