package FuncDsm
import DPD._

//case class Csv(function: String, file:String, line: Int, dependsOnFunction: String, dependsOnType: DependencyType.Value, dependsOnFile: String)

class GenDsm(csvList: List[FuncDsm.Csv]) {
    def dotPrefix(str:String):String = if(str.contains('.')) str.split('.')(0) else str
    def dotRest(str:String):String = if(str.contains('.')) str.split('.').tail.mkString(".") else str

    val packagePrefix:String =  dotPrefix(csvList.head.function)
    val dependencies:List[DependencyType.Value] = csvList.map(_.dependsOnType).distinct.sorted
    val zeroDep = (0 until dependencies.size).toList.map(_ => "0").mkString("")
    val dependsOnFunction = csvList.map(t => fqfunction(t.dependsOnFunction, t.dependsOnFile))
    lazy val functions = (csvList.map(_.function) ::: dependsOnFunction).map(noargs).distinct.sorted 

    // group all the csvList by function
    val matrix = "0 0 0 0"

    // transforms "function(Type arg)" to "function" 
    // basically masks function overloads
    def noargs(function: String):String = 
        if(function.contains('(')) 
            function.substring(0, function.indexOf('('))        
        else function

    // given a dependencyType return it's string
    def depString(dep:DependencyType.Value):String = {
        val index = dependencies.indexOf(dep)
        var rep = zeroDep.toArray;
        rep(index)= 1;
        rep.mkString;
    }
    // given a package prefix, function and it's file
    // returns the combined universal identifier for the function
    def fqfunction(function:String, file:String):String = {
        //file should start from package prefix
        val trimmedFile = file.substring(file.indexOf(packagePrefix))

        //prevent duplicate use of class name
        if(trimmedFile.endsWith(dotPrefix(function)))
            return trimmedFile + "." + dotRest(function)
        else if(trimmedFile.endsWith(function)) return trimmedFile
        else return trimmedFile + "." + function
    }

    // given the number of total number of functions, the size of dependency string,
    // generate a dependency binary string "0010" for a function againt the other functions
    def serialize(csv: List[(String, Int, String)], depSize: Int, fileSize: Int): String = {
        val depfuncs = csv.map(t => t._3)
        (0 to fileSize).toList.map(_ => "0").zipWithIndex.map(t => {
            if(depfuncs.contains(t._2)) {
                "11111"
            }
            else t._1
        }).mkString(" ")
    }

    def printStr:String = dependencies.mkString(",") + "\n" + functions.size + "\n" + matrix.mkString("\n") + "\n" + functions.mkString("\n")
}