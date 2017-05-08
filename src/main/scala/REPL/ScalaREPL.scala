import scala.tools.nsc.interpreter._
import scala.tools.nsc.Settings
import DPD.Main

object ScalaREPL {
  def main(args: Array[String]): Unit = {

    val hello = "Hello"
    // Break into debug REPL with
    ammonite.Main(
      predef = "println(\"Starting Debugging!\")"
    ).run(
      "hello" -> hello,
      "fooValue" -> foo(),
      "test" -> Main.test()
    )
  }
  def foo() = 1

  
}
