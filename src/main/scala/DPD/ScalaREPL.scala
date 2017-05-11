package DPD

object ScalaREPL {
  def main(args: Array[String]): Unit = {
    // Break into debug REPL with
    ammonite.Main(
      predef = "println(\"Starting Debugging!\")"
    ).run(
      // put dependency types constant in the environment
      "TYPED" -> DependencyType.TYPED,
      "USE" -> DependencyType.USE,
      "IMPLEMENT" -> DependencyType.IMPLEMENT,
      "EXTEND" -> DependencyType.EXTEND,
      "CALL" -> DependencyType.CALL,
      "SET" -> DependencyType.SET,
      "IMPORT" -> DependencyType.IMPORT,
      "CREATE" -> DependencyType.CREATE,
      "CAST" -> DependencyType.CAST,
      "THROW" -> DependencyType.THROW,
      "MODIFY" -> DependencyType.MODIFY,

      "$" -> Util,
      "pattern" -> Pattern,
      "dsm" -> Util.parse
    )
  }
}
