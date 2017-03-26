package DPD.Model

import Models.Matrix

/**
  * Created by Justice on 3/23/2017.
  */
object Implicits {
  implicit class _Entity(hmap: Map[Int, CNode_S]) {
    def promoteAll(): Map[Int, CNode_S] = hmap.mapValues(c => new CNode_S(c.classId, c.pocket + 1, c.score))

    def demoteAll(): Map[Int, CNode_S] = hmap.mapValues(c => new CNode_S(c.classId, c.pocket - 1, c.score))

    def promoteAll(list: List[Int]): Map[Int, CNode_S] = hmap.mapValues(c => {
      if (list.contains(c.classId))
        new CNode_S(c.classId, c.pocket + 1, c.score)
      else c
    })

    def demoteAll(list: List[Int]): Map[Int, CNode_S] = hmap.mapValues(c => {
      if (list.contains(c.classId))
        new CNode_S(c.classId, c.pocket - 1, c.score)
      else c
    })

    def hasClassId(classId: Int): Boolean = hmap.keySet.contains(classId)

    def removeClassId(classId: Int): Map[Int, CNode_S] = hmap.filterKeys(_ != classId)

    def hasPocket(pocket: Int): Boolean = hmap.values.exists(c => c.pocket == pocket)

    def removePocket(pocket: Int): Map[Int, CNode_S] = hmap.filter((t) => t._2.pocket != pocket)

    def setPocket(original:Int, newPocket:Int): Map[Int, CNode_S]  = hmap.mapValues(c => {
      if(c.pocket == original)
        new CNode_S(c.classId, newPocket, c.score)
      else c // this line is basically unnecessary
    })
  }

  implicit class _Bucket(hmap: Map[String, Map[Int, CNode_S]]) {
    def setPocket(original:Int, newPocket:Int) = hmap.mapValues(e => e.setPocket(original, newPocket))

    def hasPocket(pocket: Int) = hmap.values.exists(e => e.hasPocket(pocket))

    def getEntity(key: String) = hmap.get(key)
  }

  implicit class _List(list: List[String]) {
    def join(separator: String = ""): String = list.reduce((a, b) => a + separator + b)
  }

  implicit class _Array(array: Array[String]) {
    def join(separator: String = ""): String = array.reduce((a, b) => a + separator + b)
  }

  implicit class _DSMDataStructure_S(dsmDs: _DSMDataStructure_S) {
    def findTypes(types: String*): List[String] =
      dsmDs.files.filter(f => types.exists(f.contains))

    //(classId, size)
    def keyInterface: (Int, Int) = dsmDs.adjMatrix.flatten.collect { case t => t._1 != 0 => t._2 }.groupBy(i => i).mapValues(_.size).maxBy(_._2)      
  }

}
