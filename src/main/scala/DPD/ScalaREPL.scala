package DPD

object ScalaREPL {
  def main(args: Array[String]): Unit = {

    // if no dsm is specified, use default
    val dsmFile = if (args.length == 0) "dsm/simpleVisitorPattern.dsm" else args(0)

    // Break into debug REPL with
    ammonite.Main(
      // see ./project/predef.sc for more set up
      predef = "println(\"Starting DPD!\")"
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
      "dsm" -> Util.parse(dsmFile)
    )
  }
}
