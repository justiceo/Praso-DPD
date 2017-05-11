package DPD

import DPD.DependencyType._
import DPD.Types._
import org.scalatest._

import scala.io.Source

class DSMDataStructureSpec extends FlatSpec with Matchers {

  lazy val testDsmFile = "dsm/observer-test.dsm"
  val dsm = Util.parse(testDsmFile)
  val (dependencies, count, adjMatrix, files) = (dsm.dependencyTypes, dsm.files.size, dsm.adjMatrix, dsm.files)
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
    dsmDS.keyInterface() shouldEqual List((1, 4))
  }

  it should "return the same data when called with a count of 1" in {
    dsmDS.keyInterface(1) shouldEqual List((1, 4))
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

  "subDsm" should "return original dsm in the observer interface case" in {
    dsmDS.toString shouldEqual dsmDS._subDsm(1).toString
  }

  it should "return correct dependency type, matrix and files" in {
    val subDsm = dsmDS._subDsm(0)

    val files = subDsm.files
    assert(files.size == 2 &&
      files.exists(_.endsWith("ConcreteObserverB.java")) &&
      files.exists(_.endsWith("IObserver.java")))

    val deps = subDsm.dependencyTypes
    assert(deps.size == 1 && deps.contains(DependencyType.IMPLEMENT))

    val matrix = subDsm.adjMatrix
    assert(matrix.trio.head._1 == 1)
  }

  "dependents" should "return all classes that are dependent on the given class" in {
    dsm.dependents(1) shouldEqual List(0, 2, 3, 4)
    dsm.dependents(0) shouldEqual List.empty
  }

  it should "return all classes that have given dependencies on them" in {
    dsm.dependents(IMPLEMENT) shouldEqual List(1)
    dsm.dependents(SET) shouldEqual List.empty
    dsm.dependents(IMPLEMENT, TYPED, USE) shouldEqual List.empty
    dsm.dependents(TYPED, USE) shouldEqual List(1)
  }

  it should "return all dependent classes that have specified dependencies" in {
    dsm.dependents(1, IMPLEMENT) shouldEqual List(0, 4)
    dsm.dependents(1, TYPED, USE) shouldEqual List(2, 3)
    dsm.dependents(1, SET) shouldEqual List.empty
    dsm.dependents(0, IMPLEMENT) shouldEqual List.empty
  }


  "dependencies" should "return all classes that this class is dependent on" in {
    dsm.dependencies(1) shouldEqual List.empty
    dsm.dependencies(0) shouldEqual List(1)
    dsm.dependencies(2) shouldEqual List(1)
  }

  it should "return all classes have the given dependencies" in {
    dsm.dependencies(IMPLEMENT) shouldEqual List(0, 4)
    dsm.dependencies(SET) shouldEqual List.empty
    dsm.dependencies(IMPLEMENT, TYPED, USE) shouldEqual List.empty
    dsm.dependencies(TYPED, USE) shouldEqual List(2, 3)
    dsm.dependencies(TYPED) shouldEqual List(2, 3)
  }

  it should "return all class it has the specified dependencies on" in {
    dsm.dependencies(0, IMPLEMENT) shouldEqual List(1)
    dsm.dependencies(2, TYPED, USE) shouldEqual List(1)
    dsm.dependencies(0, TYPED, USE) shouldEqual List.empty
    dsm.dependencies(1, SET) shouldEqual List.empty
    dsm.dependencies(3, USE) shouldEqual List(1)
  }


}