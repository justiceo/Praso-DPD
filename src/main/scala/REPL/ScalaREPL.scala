import scala.tools.nsc.interpreter._
import scala.tools.nsc.Settings

object ScalaREPL {
  def main(args: Array[String]): Unit = {
    val hello = "Hello"
    // Break into debug REPL with
    ammonite.Main(
      predef = "println(\"Starting Debugging!\")"
    ).run(
      "hello" -> hello,
      "fooValue" -> foo()
    )
  }
  def foo() = 1

  
}
