package DPD

/**
  * Created by Justice on 3/23/2017.
  */
object DependencyType extends Enumeration {
  val
  TYPED,
  USE,
  IMPLEMENT,
  EXTEND,
  CALL,
  SET,
  IMPORT,
  CREATE,
  CAST,
  THROW,
  MODIFY
  = Value
}
