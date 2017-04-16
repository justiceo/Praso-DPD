package FuncDsm
import scala.io.Source
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

    case class Csv(caller: String, line: Int, dependency: DependencyType.Value, caller: String)

    def main(args: Array[String]) = {
        println("hello main works");
    }

    def run(file: String) = {
        val lines = Source.fromFile(file).getLines();
        val csv: List[Csv] = lines.map(tokenize); 
        verifyCsv(csv); 
        val dependencies: List[DependencyType.Value] = csv.map(_.dependency).distinct.sorted
        val grouped = csv.map(l => (l.caller, dependencies.indexOf(l.dependency), csv.indexOf(l.callee))).groupBy(_ => _.caller)
        val files = grouped.keySet()
        val matrix = grouped.map(t => serialize(t._2, dependencies.size))
        val dsm = dependencies.mkString(",") + "\n" + files.count + "\n" + matrix.mkString("\n") + files.makString("\n")
        println(dsm)
    }

    def serialize(csv: List[(String, Int, Int)], depSize: Int): String = {
        "dude";
    }

    def tokenize(line: String): Csv = {
        val tokens = List();
        val iter = line.iterator();
        var sb = new StringBuilder();
        while(iter.hasNext) {
            val ch = iter.next();
            if(ch == ",") {
                tokens.add(sb.toString);
                sb = new StringBuilder();
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
        Csv(tokens(0), tokens(1), tokens(2), tokens(3));
    }

    def verifyCsv(csvList: List[Csv]): Unit = {
        //all callees must end with ")"
        //all callers must end start with "D:Code",
        //the other two, Line and Dependency would throw an initialization exception otherwise
        if (! csvList.map(_.callee).every(_.endsWith(")")) ) 
            throw new FormatException("all callees must end with ')'")
        if (! csvList.map(_.callee).every(_.beginsWith("D:/Code")) ) 
            throw new FormatException("all callers must begin with 'D:/Code'")
    }
}
