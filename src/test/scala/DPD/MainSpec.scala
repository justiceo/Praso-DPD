package DPD

import org.scalatest._

class MainSpec extends FlatSpec with Matchers {

    lazy val testDsmFile = "src\\test\\resources\\test.dsm"

    "Parse dsm" should "return a 4uple of dependencies, count, matrix and files" in {
        val (dependencies, count, adjMatrix, files) = Main.parse(testDsmFile)
        dependencies shouldEqual List(DependencyType.TYPED, DependencyType.USE, DependencyType.IMPLEMENT)
        count shouldEqual 5
        adjMatrix.size shouldEqual count
        files.size shouldEqual count
    }

}