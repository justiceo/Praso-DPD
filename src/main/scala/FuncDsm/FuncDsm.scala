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
        println("hello main works")
        println(tokenize("org.apache.commons.compress.AbstractTestCase.addArchiveEntry(ArchiveOutputStream out,String filename,File infile),D:\\Code\\Tools\\art_tools\\scripts\\dump\\commons-compress\\src\\test\\java\\org\\apache\\commons\\compress\\AbstractTestCase.java,214,ArchiveOutputStream.createArchiveEntry,Call,D:\\Code\\Tools\\art_tools\\scripts\\dump\\commons-compress\\src\\main\\java\\org\\apache\\commons\\compress\\archivers\\ArchiveOutputStream.java"))
        run("D:\\Code\\IdeaProjects\\DPD\\src\\main\\resources\\func-dependency.csv")
    }

    def run(file: String) = {
        val lines = Source.fromFile(file).getLines()
        val csv: List[Csv] = lines.toList.tail.map(l => tokenize(l))
        verifyCsv(csv)
        val dependencies: List[DependencyType.Value] = csv.map(_.dependsOnType).distinct.sorted
        // thin down to csv to only the three essential components
        val thinned: List[(String, Int, String)] = csv.map(l => (l.function, dependencies.indexOf(l.dependsOnType), l.dependsOnFunction))
        // group the thinned version by functions and preserve order
        val grouped: Map[String, List[(String, Int, String)]] = thinned.groupBy(_._1)
        // extract the functions only
        val functions = grouped.keys.toList.sorted
        // extract the dependsOnFunctions and add if not exists
        // because functions that have zero dependencies but have dependents should also be here

        // merge the two lists and sort

        println(functions.mkString("\n"))
        //val matrix = grouped.map(t => serialize(t._2, dependencies.size, files.size))
        //val dsm = dependencies.mkString(",") + "\n" + files.size + "\n" + matrix.mkString("\n") + files.mkString("\n")
        //println(dsm)
    }

    def getCsvIndex(csvList: List[Csv], func: String): Int = {
        val res = csvList.map(_.function).zipWithIndex.filter(t => t._1.contains("." + func))
        if(res.isEmpty) 0
        else res.head._2
    }


    def serialize(csv: List[(String, Int, Int)], depSize: Int, fileSize: Int): String = {
        val depfuncs = csv.map(t => t._3);
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
        Csv(tokens(0), tokens(1), tokens(2).toInt, tokens(3), DependencyType.withName(tokens(4).toUpperCase()), tokens(5))
    }

    def verifyCsv(csvList: List[Csv]): Unit = {
        //all callees must end with ")"
        //all callers must end start with "D:Code",
        //the other two, Line and Dependency would throw an initialization exception otherwise
        if (csvList.map(_.function).exists(s => !s.endsWith(")")) )
            throw new Exception("all callees must end with ')'")
    }
}
