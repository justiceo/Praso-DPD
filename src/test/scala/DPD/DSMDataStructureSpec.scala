package DPD

import Models.Matrix
import org.scalatest._
import scala.io.Source

class DSMDataStructureSpec extends FlatSpec with Matchers {

    lazy val testDsmFile = "src\\test\\resources\\dsm\\test.dsm"
    lazy val (dependencies, count, adjMatrix, files) = Main.parse(testDsmFile)
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


    "toString" should "return original dsm file input (ignoring cases)" in {
        val dsmStr = dsmDS.toString.split("\n").toList.map(_.trim.toUpperCase)
        val original = Source.fromFile(testDsmFile).getLines().toList.map(_.trim.toUpperCase)
        dsmStr shouldEqual original
    }

    "getType" should "return the class name (type)" in {
        assertResult("ConcreteObserverB")(dsmDS.getType(0))
    }
    
}