package DPD

import java.io.PrintWriter
import sys.process._
import scala.io.Source
import kantan.csv._
import kantan.csv.ops._
import kantan.csv.generic._
import DPD.Types._

/** Utility functions making life on the shell easier  
  * This class is dropped whole into the shell as '$'
  * These functions interact with the OS moreso than PRASO-DPD
  * Sample use-case: To get the full path of jafka.dsm in the resource/dsm folder
  * run: $.resource("dsm/jafka.dsm")
  * output: "/home/username/project-dir/target/scala-2.12/classes/dsm/jafka.dsm" 
  */
object Util {


  /** Holds value of last pocket used */
  private var pocketCounter: Int = 10000
  /** Mechanism for generating small, yet unique ids for pockets.
   *  Returns and incremental value of `pocketCounter` with each call
   */
  def nextPocket: Int = {
    pocketCounter += 1
    pocketCounter
  }

  /** Takes the relative path of a resource (a file in /src/main/resources/ ) and returns it's absolute path.
   * Sample use-case: To get the full path of jafka.dsm in the resource/dsm folder
   * run: $.resource("dsm/jafka.dsm")
   * output: "/home/username/project-dir/target/scala-2.12/classes/dsm/jafka.dsm"
   * todo: check if file exists and print useful error than wait for OS to throw exception
   */
  def resource(file: String): (String, Error) = {
    try{
      (getClass.getClassLoader.getResource(file).getPath, Nil)
    }catch {
      case e: Exception => { return (null, e.toString() :: Nil)}
    }
  }

  /**
  * Same as _parse but accepts relative path of file in resource folder and transforms to the absolute path
  * before calling _parse
  */ 
  def parse(path: String): DSMDataStructure = _parse(resource(path))

  /**
   * Takes the absolute path to a dsm file, parse contents and returns it as a DSMDataStructure
   * A DSM file has the following structure (given the project has 10 files)
   * - A dependency line (depLine) - a string array that lists all the dependencies exhibited in the project
   * - A file count line (countLine) - a number that represents the number of files (which is also the size of the matrix) in the project
   * - The matrix lines - The zeros and ones line, their number should match file count and the width of each line should also match file count
   * - The files - List of all the files whose dependencies are in the matrix
   * - Initially, both matrix and files are stored in matrix_files list.
   */
  def _parse(path: String): DSMDataStructure = {
    // read the entire file into a list
    val lines = Source.fromFile(path).getLines().takeWhile(l => !l.startsWith("###")).toList 
    // destructure the lines since we know exactly what the first two lines will be
    val depLine :: countLine :: matrix_files = lines;
    // convert the countLine (which is a number) to real number
    val count = Integer.parseInt(countLine)

    // takes the string "[Use,Call]" and returns the list [DependencyType.Use, DependencyType.Call]
    def extractDepArray(arg: String): List[DependencyType.Value] =
      arg.replace("[", "").replace("]", "").split(",").map(x => DependencyType.withName(x.trim.toUpperCase)).toList

    // takes a square matrix of strings and returns an adjacency matrix of integers
    // i.e. takes ["0 10", "11 10"] and returns [[(1,2)], [(0,3), (1 2)]]    
    def extractDepMatrix(lines: List[String]): List[Array[(Int, Int)]] =
      lines.map(l => l.split(" ").map(c => Integer.parseInt(c, 2)).zipWithIndex.filter(t => t._1 != 0))

    new DSMDataStructure(extractDepArray(depLine), // dependency line
      extractDepMatrix(matrix_files.take(count)), // the matrix parsed
      matrix_files.takeRight(count)) // the file paths fixed
  }

  /** Saves the given DSM to a file (defaut: export.dsm) */
  def export(dsm: DSMDataStructure, filepath: String = "export.dsm"): Boolean = {
    // todo: check if file path is valid and print appropriate error than throw exception
    new PrintWriter(filepath) {
      write(dsm.toString)
      close()
    }
    true
  }

  /** Takes Github project url and clones the project to the /target directory */
  def gitClone(url:String): String = {
    // todo: validate url and reponame and throw better exceptions
    val repoName = url.substring(url.lastIndexOf("/")+1).replace(".git", "")
    val destination = "./target/" + repoName

    if(new java.io.File(destination).exists) 
      s"$repoName already exists locally in $destination"
    else {
      s"git clone $url $destination".!
      s"$repoName cloned successfully"
    }
    // return path of cloned project
    destination
  }

  /** Takes the absolute path of project's src directory and generates dsm for it
   * Note: path must be absolute, and point to the src not root dir, avoids include test classes in dsm
   */
  def genDsm(projectPath:String): String = {
    val udb = projectPath + "/project.udb"
    val cytoscape = projectPath + "/cytoscape.xml"
    val dsm = projectPath + "project.dsm";

    // todo: check if project path is valid. must exist 
    // - check if understand is installed
    // - check if genSdsm-cmd-jdk1.6.jar is located in the jars subdir

    // run the bash commands to generate the dsm. the .! at the end of the string causes it to be executed immediately
    s"/usr/bin/scitools/bin/linux64/und create -db $udb -languages java".!
    s"/usr/bin/scitools/bin/linux64/und -db $udb add $projectPath".!
    s"/usr/bin/scitools/bin/linux64/und analyze $udb".!
    s"/usr/bin/scitools/bin/linux64/und export -dependencies file cytoscape $cytoscape $udb".!
    s"java -jar ./jars/genSdsm-cmd-jdk1.6.jar -cytoscape -f $cytoscape -o $dsm > /dev/null".!

    // remove files we created in the process of generating dsm
    s"rm $udb $cytoscape".!

    // return the dsm path
    dsm
  }

  /** Parse 2.0.0: When the times come, this should parse and Class Dsm  given as csv
   * the csv format is smaller, more concise (== better to understand) and would be easier to parse too (at least much more than current dsm)
   */
  var iterator: kantan.csv.CsvReader[kantan.csv.ReadResult[List[String]]] = null
  def parse2 = {
    val rawData: java.net.URL = getClass.getResource("/Starbuzz_ClassDependencyMatrix.csv")
    val _iterator = rawData.asCsvReader[List[String]](rfc.withHeader)
    iterator = _iterator
    val files = iterator.map(t => t.get).toList
    println(files)
    //iterator.foreach(println)
    /*val size = iterator(0).size
    println(iterator(0))
    assert(!iterator.exists(a => a.size != size))
    val files = iterator.map(a => a(0))
    files.foreach(println)    */
  }
}
