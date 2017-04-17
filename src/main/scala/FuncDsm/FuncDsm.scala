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

    case class Csv(caller: String, line: Int, dependency: DependencyType.Value, callee: String)

    def main(args: Array[String]): Unit = {
        println("hello main works");
    }

    def run(file: String) = {
        val lines = Source.fromFile(file).getLines();
        val csv: List[Csv] = lines.map(l => tokenize(l)).toList; 
        verifyCsv(csv); 
        val dependencies: List[DependencyType.Value] = csv.map(_.dependency).distinct.sorted
        val grouped = csv.map(l => (l.caller, dependencies.indexOf(l.dependency), csv.indexOf(l.callee))).groupBy(_._1)
        val files = grouped.keys.toList
        val matrix = grouped.map(t => serialize(t._2, dependencies.size))
        val dsm = dependencies.mkString(",") + "\n" + files.size + "\n" + matrix.mkString("\n") + files.mkString("\n")
        println(dsm)
    }

    def serialize(csv: List[(String, Int, Int)], depSize: Int): String = {
        "dude";
    }

    def tokenize(line: String): Csv = {
        val tokens = ListBuffer[String]();
        val iter = line.iterator
        var sb = new StringBuilder()
        while(iter.hasNext) {
            var ch = iter.next()
            if(ch == ",") {
                tokens += (sb.toString)
                sb = new StringBuilder()
            }
            else if(ch == "(") {
                while(ch != ")") {
                    sb.append(ch)
                    ch = iter.next()
                }
                sb.append(ch) // add closing ")"
            }
            else sb.append(ch);
        }
        return Csv(tokens(0), tokens(1).toInt, DependencyType.withName(tokens(2)), tokens(3));
    }

    def verifyCsv(csvList: List[Csv]): Unit = {
        //all callees must end with ")"
        //all callers must end start with "D:Code",
        //the other two, Line and Dependency would throw an initialization exception otherwise
        if (csvList.map(_.callee).exists(s => s.endsWith(")")) ) 
            throw new Exception("all callees must end with ')'")
        if (csvList.map(_.callee).exists(s => !s.startsWith("D:/Code")) ) 
            throw new Exception("all callers must begin with 'D:/Code'")
    }
}
