package DPD

import org.scalatest._

class MainSpec extends FlatSpec with Matchers {

  lazy val testDsmFile = "dsm/observer-test.dsm"

  "Parse dsm" should "return a 4uple of dependencies, count, matrix and files" in {
    val dsm = Util.parse(testDsmFile)
    val (dependencies, count, adjMatrix, files) = (dsm.dependencyTypes, dsm.files.size, dsm.adjMatrix, dsm.files)
    dependencies shouldEqual List(DependencyType.TYPED, DependencyType.USE, DependencyType.IMPLEMENT)
    count shouldEqual 5
    adjMatrix.size shouldEqual count
    files.size shouldEqual count
  }

}