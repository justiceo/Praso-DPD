package FuncDsm
import scala.io.Source
import scala.collection.mutable.ListBuffer
import DPD._

// read all lines in file
// use tokenizer to break up into csv columns
// parse csv into a case class
// verify parsed csv is valid
// map the "other" function to its index in the list
// map the "dependency" to a string representation
// group the lines by function
// the group list keys become the file (function list) list
// the group list values is mapped to a line (string) representation
// print the depenecy, size, matrix and functions list

object FuncDsm {

    case class Csv(function: String, file:String, line: Int, dependsOnFunction: String, dependsOnType: DependencyType.Value, dependsOnFile: String)

    def main(args: Array[String]): Unit = {
        val genDsm = new GenDsm(getCsvFromFile(getFilePath("simpleProject.csv")))
        println(genDsm.printStr)
        println("\n" + genDsm.funcs_with_dep)
        //run(getFilePath("func-dependency.csv"))
    }
    
    def getFilePath(file: String): String = getClass.getClassLoader.getResource(file).getPath

    def fixDependsOn(hostfunc: String, dependsOnFunction: String, dependsOnFile: String): String = {
        // units the file and function into one for better matching
        // convert D:\Code\sample.java to D:.Code.sample
        val trimmedFile = dependsOnFile.replace(".java", "").replace("\\", ".")
        val funcPrefix =
            if (dependsOnFunction.indexOf(".") > -1)
                dependsOnFunction.substring(0, dependsOnFunction.indexOf("."))
            else dependsOnFunction
        val pkgPrefix = hostfunc.substring(0, hostfunc.indexOf("."))
        // remove everything before package prefix
        val depFunc =
            if(trimmedFile.indexOf(pkgPrefix) > -1)
                trimmedFile.substring(trimmedFile.indexOf(pkgPrefix))
            else trimmedFile

        // if the dep function already end's with class name, replace it with full function
        if(depFunc.endsWith(funcPrefix)) {
            val cut = depFunc.lastIndexOf(funcPrefix)
            depFunc.substring(cut) + dependsOnFunction
        }
        // otherwise append it
        else depFunc + "." + dependsOnFunction
    }

    // transforms "function(Type arg)" to "function" 
    // basically masks function overloads
    def noargs(function: String): String = {
        
        if(function.contains('(')) {
            function.substring(0, function.indexOf('('))
        }
        else function
    }

    // given a package prefix, function and it's file
    // returns the combined universal identifier for the function
    def fqfunction() = {

    }

    def getCsvFromFile(file: String) = {
        val lines = Source.fromFile(file).getLines()
        lines.toList.tail.map(l => tokenize(l))
    }

    def run(file: String) = {
        val lines = Source.fromFile(file).getLines()
        val csv: List[Csv] = lines.toList.tail.map(l => tokenize(l))
        verifyCsv(csv)
        val dependencies: List[DependencyType.Value] = csv.map(_.dependsOnType).distinct.sorted

        // thin down to csv to only the three essential components
        val thinned: List[(String, Int, String)] = csv.map(l => (noargs(l.function), dependencies.indexOf(l.dependsOnType), fixDependsOn(l.function, l.dependsOnFunction, l.dependsOnFile)))
        // group the thinned version by functions and preserve order
        val grouped: Map[String, List[(String, Int, String)]] = thinned.groupBy(_._1)
        // extract the functions only
        val functions = grouped.keys.toList.sorted
        // extract the dependsOnFunctions and add if not exists
        // because functions that have zero dependencies but have dependents should also be here

        // merge the two lists and sort

        //println(functions.mkString("\n"))
        val matrix = grouped.map(t => serialize(t._2, dependencies.size, functions.size))
        val dsm = dependencies.mkString(",") + "\n" + functions.size + "\n" + matrix.mkString("\n") + functions.mkString("\n")
        println(dsm)
    }

    def getCsvIndex(csvList: List[Csv], func: String): Int = {
        val res = csvList.map(_.function).zipWithIndex.filter(t => t._1.contains("." + func))
        if(res.isEmpty) 0
        else res.head._2
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

    def tokenize(line: String): Csv = {
        val tokens = ListBuffer[String]()
        val iter = line.iterator
        var sb = new StringBuilder()
        while(iter.hasNext) {
            var ch = iter.next()
            if(ch == ',') {
                tokens += sb.toString
                sb = new StringBuilder()
            }
            else if(ch == '(') {
                while(ch != ')') {
                    sb.append(ch)
                    ch = iter.next()
                }
                sb.append(ch) // add closing ")"
            }
            else sb.append(ch)
        }
        tokens += sb.toString // empty string buffer
        def dotFile(file:String):String = file.replace(".java", "").replace("\\", ".")
        Csv(tokens(0), tokens(1), tokens(2).toInt, tokens(3), DependencyType.withName(tokens(4).toUpperCase()), dotFile(tokens(5)))
    }

    def verifyCsv(csvList: List[Csv]): Unit = {
        //all callees must end with ")"
        //all callers must end start with "D:Code",
        //the other two, Line and Dependency would throw an initialization exception otherwise
        if (csvList.map(_.function).exists(s => !s.endsWith(")")) )
            throw new Exception("all callees must end with ')'")
    }
}
