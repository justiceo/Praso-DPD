package DPD

import org.scalatest._
import scala.io.Source

class DSMDataStructureSpec extends FlatSpec with Matchers {

    lazy val testDsmFile = "dsm/observer-test.dsm"
    val dsm = Util.parse(testDsmFile)
    val (dependencies, count, adjMatrix, files)  = (dsm.dependencyTypes, dsm.files.size, dsm.adjMatrix, dsm.files)
    lazy val dsmDS = new DSMDataStructure(dependencies, adjMatrix, files)

    // property tests
    "DSMDataStructure" should "have equal matrix and file sizes" in {
        dsmDS.adjMatrix.size shouldEqual dsmDS.files.size
    }

    it should "have a size property equal to matrix size" in {
        dsmDS.size shouldEqual dsmDS.adjMatrix.size
    }

    it should "have a sourceRoot that matches path up till package names" in {
        dsmDS.sourceRoot shouldEqual "D:\\Code\\IdeaProjects\\DesignPatterns\\src\\CommonPatterns\\observer\\"
    }

    "keyInterface" should "return (top interface, dependency count) when called without args" in {
        dsmDS.keyInterface() shouldEqual List((1,4))
    }

    it should "return the same data when called with a count of 1" in {
        dsmDS.keyInterface(1) shouldEqual List((1,4))
    }

    it should "return an empty list when called with 0" in {
        dsmDS.keyInterface(0) shouldEqual List()
    }

    it should "return the keyInterface with the next inorder interface when there is no mode" in {
        // data set is unsuitable for this test, as there is only one group where all classes fall into
        //dsmDS.keyInterface(2) shouldEqual List((1,4), (0,0))
    }


    "toString" should "return original dsm file input (ignoring cases)" in {
        val dsmStr = dsmDS.toString.split("\n").toList.map(_.trim.toUpperCase)
        val original = Source.fromFile(Util.resource(testDsmFile)).getLines().toList.map(_.trim.toUpperCase)
        dsmStr shouldEqual original
    }

    "getType" should "return the class name (type)" in {
        assertResult("ConcreteObserverB")(dsmDS.nice(0))
    }

    it should "return original dsm in the observer interface case" in {
        dsmDS.toString shouldEqual dsmDS._subDsm(1).toString
    }
    
}