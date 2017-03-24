package DPD.Browser

import DPD.Model.DependencyType_S
import Models._

/**
  * Created by Justice on 3/23/2017.
  */
class DSMDataStructure_S (val dependencies: List[DependencyType_S.Value],
                          val adjMatrix: Matrix,
                          val files: List[String]) {

  val size: Int = adjMatrix.length
  def subDsm(classId: Int): DSMDataStructure_S = {
    null
  }

  override def toString: String = {
    // get dependency line
    def getDepLine: String = "[" + dependencies.map(_ toString).reduce((a, b) => a + "," + b) + "]"

    //dependencies.map((t) => t._1.toString).reduce((a, b) => a + "," + b)
    def expandMatrix: Matrix  = adjMatrix.map(arr => {
      val (el, indices) = arr.unzip
      Array.range(0, size).map(i => {
        if(indices.contains(i))
          (el(indices.indexOf(i)), i)
        else (0, i)
      })
    })

    def flattenMatrix(adjMatrix: Matrix): List[String] =
      adjMatrix.map(_.map((t) => Integer.toBinaryString(t._1)).map(s => {
        val diff = dependencies.size - s.length
        if(s.equals("0") || diff == 0) s
        else (0 to diff).map(_ => "0").reduce((a,b) => a+b) + s
      }).reduce((a,b) => a + " " + b))

    // generate (0,0) to (0,n) tuples, then map and insert data, then flatten numbers, reduce numbers
    // flatten the matrix to string

    s"${getDepLine} \n$size \n${files.reduce((a,b)=> a+"\n"+b)}"

  }


}
