package DPD

import Models.Matrix
import org.scalatest._

class DSMDataStructureSpec extends FlatSpec with Matchers {

    lazy val fixture = new {
        val dependencies: List[DependencyType.Value] = List(DependencyType.EXTEND, DependencyType.USE)
        val matrix: Matrix = List(Array((1,0)), Array((1,1)))
        val files: List[String] = List("d:/code/file/test.java", "d:/code/file/another.java")
    }

    lazy val dsm = new DSMDataStructure(fixture.dependencies, fixture.matrix, fixture.files)

    "The Hello Object" should "say hello" in {
        val f = fixture
        val dsmDS = new DSMDataStructure(f.dependencies, f.matrix, f.files)
        "hello" shouldEqual "hello"
    }
}