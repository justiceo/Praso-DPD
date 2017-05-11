package FuncDsm

import DPD._

//case class Csv(function: String, file:String, line: Int, dependsOnFunction: String, dependsOnType: DependencyType.Value, dependsOnFile: String)

class GenDsm(csvList: List[FuncDsm.Csv]) {
  def dotPrefix(str: String): String = if (str.contains('.')) str.split('.')(0) else str

  def dotRest(str: String): String = if (str.contains('.')) str.split('.').tail.mkString(".") else str

  val packagePrefix: String = dotPrefix(csvList.head.function)
  val dependencies: List[DependencyType.Value] = csvList.map(_.dependsOnType).distinct.sorted
  val zeroDep = dependencies.indices.toList.map(_ => "0").mkString
  val dependsOnFunction = csvList.map(t => fqfunction(t.dependsOnFunction, t.dependsOnFile))
  lazy val functions = (csvList.map(_.function) ::: dependsOnFunction).map(noargs).distinct.sorted
  lazy val zeroDepFunc: List[String] = functions.indices.toList.map(_ => "0")

  // group all the csvList by function
  val thinned: List[(String, String, String)] = csvList.map(t => (noargs(t.function), depString(t.dependsOnType), fqfunction(t.dependsOnFunction, t.dependsOnFile)))
  val grouped: Map[String, List[(String, String, String)]] = thinned.groupBy(_._1)
  val funcs_with_dep = grouped.keys.toList

  val matrix = functions.map(f => {
    // if it exists, then it has dependencies
    if (funcs_with_dep.contains(f)) {
      var zerod = zeroDepFunc.toArray
      grouped.get(f) match {
        case Some(a) => {
          a.foreach(t => {
            val index = functions.indexOf(t._3)
            zerod(index) = t._2
          })
        }
        case None => throw new Exception("list shouldn't be empty'")
      }
      zerod.mkString(" ")
    }
    // otherwise, it doesn't, print zeros
    else zeroDepFunc.mkString(" ")
  })

  // transforms "function(Type arg)" to "function"
  // basically masks function overloads
  def noargs(function: String): String =
  if (function.contains('('))
    function.substring(0, function.indexOf('('))
  else function

  // given a dependencyType return it's string
  def depString(dep: DependencyType.Value): String = {
    val index = dependencies.indexOf(dep)
    var rep = zeroDep.toArray
    rep(index) = '1'
    rep.mkString
  }

  // given a package prefix, function and it's file
  // returns the combined universal identifier for the function
  def fqfunction(function: String, file: String): String = {
    //file should start from package prefix
    val trimmedFile = if (file.indexOf(packagePrefix) > -1) file.substring(file.indexOf(packagePrefix)) else file

    //prevent duplicate use of class name
    if (trimmedFile.endsWith(dotPrefix(function)))
      return trimmedFile + "." + dotRest(function)
    else if (trimmedFile.endsWith(function)) return trimmedFile
    else return trimmedFile + "." + function
  }

  def printStr: String = dependencies.mkString(",") + "\n" + functions.size + "\n" + matrix.mkString("\n") + "\n" + functions.mkString("\n")
}