package DPD.Model

/**
  * Created by Justice on 3/23/2017.
  */
class CNode_S(val classId: Int, val pocket: Int, val score:Int = 0) {

  override def toString: String = s"{classId: $classId, pocket: $pocket}"
}
