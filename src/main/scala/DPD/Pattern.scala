package DPD

import DPD.DependencyType._
import DPD.Types._

import scala.language.postfixOps

object Pattern {
  
  def _observer(dsm: DSMDataStructure): Map[String, Entity] = {
    // observer interfaces must be extended by concrete observers, typed and called by subjects
    val (sub: Entity, sup: Entity) = dsm.SPECIALIZE.asEntities.inGroups
    val obsI = sup.thatIs(List(TYPED, USE), dsm)
    // some pockets are removed at this point
    val conO = obsI.subClasses(sub)
    val (subj, observerI) = dsm.classesThat(List(TYPED, USE), obsI) asEntities

    Map("Observer Interface" -> obsI,
      "Concrete Observer" -> conO,
      "Subject" -> subj)
  }

  def observer(dsm: DSMDataStructure): Map[String, List[String]] = nice(_observer(dsm), dsm)

  def _visitor(dsm: DSMDataStructure): Map[String, Entity] = {
    val (sub, sup) = dsm.SPECIALIZE.asEntities inGroups
    val elementsP = sup.that(List(TYPED), sup, dsm)
    val visitorP = sup.that(List(TYPED), sub, dsm)
    val concVisitor = visitorP.subClasses(sub)
    val concEle = elementsP.subClasses(sub)

    Map("Visitor Interface" -> visitorP,
      "Concrete Visitor" -> concVisitor,
      "Element" -> elementsP,
      "Concrete Element" -> concEle)
  }

  def visitor(dsm: DSMDataStructure): Map[String, List[String]] = nice(_visitor(dsm), dsm)

  def nice(res: Map[String, Entity], dsm: DSMDataStructure): Map[String, List[String]] = {
    var m: Map[String, List[String]] = Map()
    res foreach { case (name, entity) => {
      val entStr: List[String] = entity.map(c => dsm.nice(c.classId))
      m += (name -> entStr)
    }
    }
    m
  }

  def _decorator(dsm: DSMDataStructure): Map[String, Entity] = {
    val (sub, sup) = dsm.SPECIALIZE.asEntities.inGroups
    val decorator = sup andIn sub
    val concDecorator = decorator subClasses sub
    val component = decorator superClasses sup
    val concComponent = component.subClasses(sub).exclude(decorator, concDecorator)

    Map("Component" -> component,
      "Decorator" -> decorator,
      "Concrete Component" -> concComponent,
      "Concrete Decorator" -> concDecorator)
  }

  def decorator(dsm: DSMDataStructure): Map[String, List[String]] = nice(_decorator(dsm), dsm)

  def _composite(dsm: DSMDataStructure): Map[String, Entity] = {
    val (sup, sub) = dsm.SPECIALIZE.atLeast(3) asEntities
    val composite = sub.that(List(TYPED), sup, dsm)
    val component = composite superClasses sup
    val leaf = sub exclude composite

    //removeEmptyPockets()
    Map("Composite" -> composite,
      "Component" -> component,
      "Leaf" -> leaf)
  }

  def composite(dsm: DSMDataStructure): Map[String, List[String]] = nice(_composite(dsm), dsm)

  def runAll(dsm: DSMDataStructure): Map[String, Map[String, List[String]]] =
    Map("Observer Pattern" -> observer(dsm),
      "Visitor Pattern" -> visitor(dsm),
      "Decorator Pattern" -> decorator(dsm),
      "Composite Pattern" -> composite(dsm))

}