package DPD

import DPD.DependencyType._
import DPD.Types._
import scala.language.postfixOps

object Pattern { 

  object Flag extends Enumeration {
    val
    IgnoreTest,
    RemoveAnyEmpty,
    RemoveAllEmpty
    = Value
  } 
  
  def observer(dsm: DSMDataStructure): Map[String, List[String]] = nice(removeTests(_observer(dsm), dsm), dsm)
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


  def visitor(dsm: DSMDataStructure): Map[String, List[String]] = nice(removeTests(_visitor(dsm), dsm), dsm)
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

  def decorator(dsm: DSMDataStructure): Map[String, List[String]] = nice(removeTests(_decorator(dsm), dsm), dsm)
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

  def composite(dsm: DSMDataStructure): Map[String, List[String]] = nice(removeTests(_composite(dsm), dsm), dsm)
  def _composite(dsm: DSMDataStructure): Map[String, Entity] = {
    val (sub, sup) = dsm.SPECIALIZE.asEntities.inGroups
    val composite = sub.that(List(CALL), sup, dsm)
    val component = composite superClasses sup
    val leaf = sub exclude composite

    //removeEmptyPockets()
    Map("Composite" -> composite,
      "Component" -> component,
      "Leaf" -> leaf)
  }

  def absFactory(dsm: DSMDataStructure): Map[String, List[String]] = nice(removeTests(_absFactory(dsm), dsm), dsm)
  def _absFactory(dsm: DSMDataStructure): Map[String, Entity] = {
    val (sub, sup) = dsm.SPECIALIZE.asEntities.inGroups
    val concreteFactory = sub.that(List(TYPED), sup, dsm).that(List(CREATE), sub, dsm)
    val abstractFactory = concreteFactory.superClasses(sup)
    val abstractProduct = sup.thatIs(List(TYPED), abstractFactory, dsm)
    val concreteProduct = abstractProduct.subClasses(sub)

    Map(
      "Abstract Factory" -> abstractFactory,
      "Concrete Factory" -> concreteFactory,
      "Abstract Product" -> abstractProduct,
      "Concrete Product" -> concreteProduct
    )
  }

  def brdige(dsm: DSMDataStructure): Map[String, List[String]] = nice(removeTests(_brdige(dsm), dsm), dsm)
  def _brdige(dsm: DSMDataStructure): Map[String, Entity] = {
    val (sub, sup) = dsm.SPECIALIZE.asEntities.inGroups
    //removeEmptyPockets()
    Map()
  }

  def builder(dsm: DSMDataStructure): Map[String, List[String]] = nice(removeTests(_builder(dsm), dsm), dsm)
  def _builder(dsm: DSMDataStructure): Map[String, Entity] = {
    val (sub, sup) = dsm.SPECIALIZE.asEntities.inGroups
    //removeEmptyPockets()
    Map()
  }

  def factoryMethod(dsm: DSMDataStructure): Map[String, List[String]] = nice(removeTests(_factoryMethod(dsm), dsm), dsm)
  def _factoryMethod(dsm: DSMDataStructure): Map[String, Entity] = {
    val (sub, sup) = dsm.SPECIALIZE.asEntities.inGroups
    //removeEmptyPockets()
    Map()
  }

  def facade(dsm: DSMDataStructure): Map[String, List[String]] = nice(_facade(dsm), dsm)
  def _facade(dsm: DSMDataStructure): Map[String, Entity] = {
    val (sub, sup) = dsm.SPECIALIZE.asEntities.inGroups
    //removeEmptyPockets()
    Map()
  }

  

  def adapter(dsm: DSMDataStructure): Map[String, List[String]] = nice(removeTests(_adapter(dsm), dsm), dsm)
  def _adapter(dsm: DSMDataStructure): Map[String, Entity] = {
    val (sub, sup) = dsm.SPECIALIZE.asEntities.inGroups
    //removeEmptyPockets()
    Map()
  }

  def _runAll(dsm: DSMDataStructure): Map[String, Map[String, List[String]]] = {
    // IgnoreTest flag can be processed here (... or passed on)
    // RemoveAnyEmpty flag can be processed here
    // RemoveAllEmpty flag can be processed here
    // Pockets cannot be processed here - diff return type
    Map("Observer Pattern" -> observer(dsm),
      "Visitor Pattern" -> visitor(dsm),
      "Decorator Pattern" -> decorator(dsm),
      "Composite Pattern" -> composite(dsm),
      "Abstract Factory" -> absFactory(dsm),
      "Bridge Pattern" -> brdige(dsm),
      "Builder Pattern" -> builder(dsm),
      "Factory Method" -> factoryMethod(dsm),
      "Facada" -> facade(dsm),
      "Adapter Pattern" -> adapter(dsm)
      )
  }

  def runAll(dsm: DSMDataStructure): Map[String, Map[String, List[String]]] = {
    _runAll(dsm).filter(t => !t._2.isEmpty && !t._2.exists(s => s._2.isEmpty))
  }

  def removeTests(pattern: Map[String, Entity], dsm:DSMDataStructure): Map[String, Entity] = {
    // for each entity, filter out nodes that are tests
    var res: Map[String, Entity] = Map()
    for((name, entity) <- pattern)
      res += ( name -> entity.filterNot(a  => dsm.isTestClass(a.classId)))    
    res
  }
    
  def nice(res: Map[String, Entity], dsm: DSMDataStructure): Map[String, List[String]] = {
    var m: Map[String, List[String]] = Map()
    res foreach { case (name, entity) => {
      val entStr: List[String] = entity.map(c => dsm.nice(c.classId))
      m += (name -> entStr)
    }}
    m
  }
}