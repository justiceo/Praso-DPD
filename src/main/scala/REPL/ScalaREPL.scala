import DPD.Util

object ScalaREPL {
  def main(args: Array[String]): Unit = {
    // Break into debug REPL with
    ammonite.Main(
      predef = "println(\"Starting Debugging!\")"
    ).run(
      "$" -> Util
    )
  }
}
