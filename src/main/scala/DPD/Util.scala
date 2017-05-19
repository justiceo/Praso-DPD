package DPD

import java.io.PrintWriter
import sys.process._
import scala.io.Source

/**
  * Created by Justice on 3/23/2017.
  */
object Util {
  var pocketCounter: Int = 10000

  def nextPocket: Int = {
    pocketCounter += 1
    pocketCounter
  }

  def resource(file: String): String = getClass.getClassLoader.getResource(file).getPath

  def parse(path: String): DSMDataStructure = _parse(resource(path))

  /** takes a dsm file as input and returns a DSM data structure */
  def _parse(path: String): DSMDataStructure = {
    val depLine :: countLine :: matrix_files = Source.fromFile(path).getLines().takeWhile(l => !l.startsWith("###")).toList
    val count = Integer.parseInt(countLine)

    def extractDepArray(arg: String): List[DependencyType.Value] =
      arg.replace("[", "").replace("]", "").split(",").map(x => DependencyType.withName(x.trim.toUpperCase)).toList

    def extractDepMatrix(lines: List[String]): List[Array[(Int, Int)]] =
      lines.map(l => l.split(" ").map(c => Integer.parseInt(c, 2)).zipWithIndex.filter(t => t._1 != 0))

    new DSMDataStructure(extractDepArray(depLine), // dependency line
      extractDepMatrix(matrix_files.take(count)), // the matrix parsed
      matrix_files.takeRight(count)) // the file paths fixed
  }

  def export(dsm: DSMDataStructure, filepath: String = "export.dsm"): Boolean = {
    new PrintWriter(filepath) {
      write(dsm.toString)
      close()
    }
    true
  }

  def gitClone(url:String): String = {
    val repoName = url.substring(url.lastIndexOf("/")+1).replace(".git", "")
    val destination = "./target/" + repoName

    if(new java.io.File(destination).exists) 
      s"$repoName already exists locally in $destination"
    else {
      s"git clone $url $destination".!
      s"$repoName cloned successfully"
    }
  }

  def genDsm(projectPath:String): String = {
    val udb = projectPath + "/project.udb"
    val cytoscape = projectPath + "/cytoscape.xml"
    val dsm = projectPath + "project.dsm"

    s"/usr/bin/scitools/bin/linux64/und create -db $udb -languages java > /dev/null".!
    s"/usr/bin/scitools/bin/linux64/und -db $udb add $projectPath > /dev/null".!
    s"/usr/bin/scitools/bin/linux64/und analyze $udb > /dev/null".!
    s"/usr/bin/scitools/bin/linux64/und export -dependencies file cytoscape $cytoscape $udb > /dev/null".!
    s"java -jar ./jars/genSdsm-cmd-jdk1.6.jar -cytoscape -f $cytoscape -o $dsm > /dev/null".!

    "Done generating dsm"
  }
}
