package DPD

object ScalaREPL {
  val usage = """
    ScalaREPL 1.0.0
    Usage: sbt "run-main DPD.ScalaREPL [--dsm|--local|--remote] path [--no-shell]"
  """
  def main(args: Array[String]): Unit = {
    if(args.length == 0) {
      println(usage);
      return;
    }

    var noShell = false   // when true, simply run Pattern.runAll() and exit without launching shell session
    var dsmFile = ""      // the dsm file to use, if not specified, can be generated by --local and --remote
    var localPath = ""    // the path to a local project from which to generate the dsm
    var remotePath = ""   // the path to a remote project (using Git VCS) from which to generate the dsm

    // parse the command line arguments
    args.sliding(2, 1).toList.collect {
      case Array("--dsm", path:String) => dsmFile = path
      case Array("--local", path:String) => localPath = path
      case Array("--remote", path:String) => remotePath = path
      case Array("--no-shell", _) => noShell = true
      case Array(_, "--no-shell") => noShell = true
    }

    // get the dsm (one way or another)
    val dsm:DSMDataStructure = {
      if (dsmFile ne "") Util.parse(dsmFile)
      else if(localPath ne "") Util.parse(Util.genDsm(localPath))
      else if(remotePath ne "") Util.parse(Util.genDsm(Util.gitClone(remotePath)))
      else null;
    }

    // if noShell, run all the patterns and exit
    if (noShell) { // run pattern.all
      println(Pattern.runAll(dsm))
      return
    }

    // otherwise, let's go deep water diving
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
      "dsm" -> dsm
    )
  }
}
