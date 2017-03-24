package DPD.Browser

import DPD.Model.DependencyType_S

/**
  * Created by Justice on 3/23/2017.
  */
class DSMDataStructure_S (val dependencies: List[(DependencyType_S.Value, Int)],
                          val adjMatrix: List[Array[(Int, Int)]],
                          val files: List[String]) {

  val size: Int = adjMatrix.length
  def subDsm(classId: Int): DSMDataStructure_S = {
    null
  }

  override def toString: String = {
    // get dependency line
    def getDepLine: String = "[" + dependencies.map((t) => t._1.toString).reduce((a, b) => a + "," + b) + "]"

    //dependencies.map((t) => t._1.toString).reduce((a, b) => a + "," + b)
    def getMatrix: List[Array[(Int, Int)]]  = adjMatrix.map(arr => {
      val (el, indices) = arr.unzip
      Array.range(0, size).map(i => {
        if(indices.contains(i))
          (el(indices.indexOf(i)), i)
        else (0, i)
      })
    })
    // generate (0,0) to (0,n) tuples, then map and insert data, then flatten numbers, reduce numbers
    // flatten the matrix to string

    null

  }


}
