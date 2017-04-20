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
    }
    
    def getFilePath(file: String): String = getClass.getClassLoader.getResource(file).getPath

    def getCsvFromFile(file: String) = {
        val lines = Source.fromFile(file).getLines()
        lines.toList.tail.map(l => tokenize(l))
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
}
