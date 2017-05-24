package DPD

import DPD.DependencyType._
import DPD.Types._
import scala.language.postfixOps

object Pattern { 
  
  def observer(dsm: DSMDataStructure): Map[String, List[String]] = nice(removeTests(_observer(dsm), dsm), dsm)
  def _observer(dsm: DSMDataStructure): Map[String, Entity] = {
    // observer interfaces must be extended by concrete observers, typed and called by subjects
    val (sub: Entity, sup: Entity) = dsm.SPECIALIZE.asEntities.inGroups
    val obsI = sup.thatIs(TYPED, USE, dsm)
    // some pockets are removed at this point
    val conO = obsI.subClasses(sub)
    val (t_subj, observerI) = dsm.classesThat(List(TYPED, USE), obsI) asEntities
    val subj = obsI.reconcile(observerI).zipReconcile(t_subj)

    Map("Observer Interface" -> obsI,
      "Concrete Observer" -> conO,
      "Subject" -> subj)
  }


  def visitor(dsm: DSMDataStructure): Map[String, List[String]] = nice(removeTests(_visitor(dsm), dsm), dsm)
  def _visitor(dsm: DSMDataStructure): Map[String, Entity] = {
    val (sub, sup) = dsm.SPECIALIZE.asEntities inGroups
    val elementsP = sup.that(TYPED, sup, dsm)
    val concEle = elementsP.subClasses(sub)
    val visitorP = sup.that(TYPED, concEle, dsm)
    val concVisitor = visitorP.subClasses(sub)

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
    val composite = sub.that(CALL, sup, dsm)
    val component = composite superClasses sup
    val leaf = sub exclude composite

    //removeEmptyPockets()
    Map("Composite" -> composite,
      "Component" -> component,
      "Leaf" -> leaf)
  }

  def absFactoryP(dsm: DSMDataStructure): Seq[Map[String, Entity]] =  _absFactoryP(dsm).filter(t => !inCompletePattern(t))
  def _absFactoryP(dsm: DSMDataStructure): Seq[Map[String, Entity]] = {
    val trimmed = removeTests(_absFactory(dsm), dsm)
    val pockets = trimmed.values.flatten.map(c => c.pocket).toList.distinct
    for(p <- pockets) yield mapPocket(trimmed, p)
  }
  def absFactory(dsm: DSMDataStructure): Map[String, List[String]] = nice(removeTests(_absFactory(dsm), dsm), dsm)
  def _absFactory(dsm: DSMDataStructure): Map[String, Entity] = {
    val (sub, sup) = dsm.SPECIALIZE.asEntities.inGroups
    val concreteFactory = sub.that(TYPED, sup, dsm).that(CREATE, sub, dsm)
    val abstractFactory = concreteFactory.superClasses(sup)
    val abstractProduct = sup.thatIs(TYPED, abstractFactory, dsm)
    val concreteProduct = abstractProduct.subClasses(sub)

    Map(
      "Abstract Factory" -> abstractFactory,
      "Concrete Factory" -> concreteFactory,
      "Abstract Product" -> abstractProduct,
      "Concrete Product" -> concreteProduct
    )
  }

  def bridge(dsm: DSMDataStructure): Map[String, List[String]] = nice(removeTests(_bridge(dsm), dsm), dsm)
  def _bridge(dsm: DSMDataStructure): Map[String, Entity] = {
    val (sub, sup) = dsm.SPECIALIZE.asEntities.inGroups
    val abstraction = sup.that(TYPED, sup, dsm)
    val refinedAbs = abstraction.subClasses(sub)
    val implementor = sup.thatIs(TYPED, abstraction, dsm).exclude(abstraction)
    val concImpl = implementor subClasses sub

    Map(
      "Abstraction" -> abstraction,
      "Refined Abstraction" -> refinedAbs,
      "Implementor" -> implementor,
      "Concrete Implementor" -> concImpl
    )
  }

  def runAll(dsm: DSMDataStructure): Map[String, Map[String, List[String]]] = {
    _runAll(dsm).filter(t => !t._2.isEmpty && !t._2.exists(s => s._2.isEmpty))
  }

  def runAllIsolated(dsm: DSMDataStructure): Map[String, Map[String, List[String]]] = {
    // isolate each pattern and run
    _runAll(dsm).filter(t => !t._2.isEmpty && !t._2.exists(s => s._2.isEmpty))
  }

  def _runAll(dsm: DSMDataStructure): Map[String, Map[String, List[String]]] = {    
    Map(
      "Observer Pattern" -> observer(dsm),
      "Visitor Pattern" -> visitor(dsm),
      "Decorator Pattern" -> decorator(dsm),
      "Composite Pattern" -> composite(dsm),
      "Abstract Factory" -> absFactory(dsm),
      "Bridge Pattern" -> bridge(dsm)
      /*
      "Builder Pattern" -> builder(dsm),
      "Factory Method" -> factoryMethod(dsm),
      "Facada" -> facade(dsm),
      "Adapter Pattern" -> adapter(dsm)*/
      )
  }

  def inCompletePattern(pattern: Map[String, Entity]): Boolean = pattern.isEmpty || pattern.exists(mapping => mapping._2.isEmpty)

  def removeTests(pattern: Map[String, Entity], dsm:DSMDataStructure): Map[String, Entity] = 
    pattern.transform((k,e) => e.filterNot(a => dsm.isTestClass(a.classId)) )
      
  def nice(pattern: Map[String, Entity], dsm: DSMDataStructure): Map[String, List[String]] = 
    pattern.transform((k,e) => e.map(c => dsm.nice(c.classId)))
  
  def mapPocket(pattern: Map[String, Entity], pocket: Int): Map[String, Entity] = 
    pattern.transform((k,e) => e.filter(c => c.pocket == pocket))
  

  

  ////////////////////
  /// Ignored patterns
  ////////////////////




  /**
   * Ignored for now
   * - very similar to observer, resume after improved precision methods, scoring and pockets
   * - incorrect implementation in java-design-patterns - resume after update java-design-pattern dsm
   */
  def builder(dsm: DSMDataStructure): Map[String, List[String]] = nice(removeTests(_builder(dsm), dsm), dsm)
  def _builder(dsm: DSMDataStructure): Map[String, Entity] = {
    val (sub, sup) = dsm.SPECIALIZE.asEntities.inGroups
    //val concBuilder = sub.that(CREATE, dsm)
    val dir = dsm.classesThat(List(TYPED), sup)
    val product = dsm.classesThat(List(CREATE), sub)
    //removeEmptyPockets()
    Map(
      "Director" -> List(),
      "Builder" -> List(),
      "Concrete Builder" -> List(),
      "Product" -> List()
      )
  }

  /** 
   * Ignore for now
   * - very similar to abstract factory
   */
  def factoryMethod(dsm: DSMDataStructure): Map[String, List[String]] = nice(removeTests(_factoryMethod(dsm), dsm), dsm)
  def _factoryMethod(dsm: DSMDataStructure): Map[String, Entity] = {
    val (sub, sup) = dsm.SPECIALIZE.asEntities.inGroups
    val concreteCreator = sub.that(TYPED, sup, dsm).that(CREATE, sub, dsm)
    val creator = concreteCreator.superClasses(sup)
    val abstractProduct = sup.thatIs(TYPED, creator, dsm)
    val concreteProduct = abstractProduct.subClasses(sub)

    Map(
      "Creator" -> creator,
      "Concrete Creator" -> concreteCreator,
      "Product" -> abstractProduct,
      "Concrete Product" -> concreteProduct
    )
  }

  /**
   * Ignore for now
   * - looks a lot like observer pattern
   * - less applicable
   * - counts as spice
   */
  def facade(dsm: DSMDataStructure): Map[String, List[String]] = nice(_facade(dsm), dsm)
  def _facade(dsm: DSMDataStructure): Map[String, Entity] = {
    val (sub, sup) = dsm.SPECIALIZE.asEntities.inGroups
    val (f1, sub2) = dsm.classesThat(List(CREATE), sub) asEntities
    val f2 = sub.reconcile(sub2).zipReconcile(f1)
    val f3 = f2.thatIs(CALL, TYPED, dsm)
    Map(
      "Facade" -> f3,
      "Sub components" -> sub2
      ) // update the pockets
  }

  
  /**
   * Ignore for now
   * - looks much like observer 
   * - would require higher-level function decomposition
   */
  def adapter(dsm: DSMDataStructure): Map[String, List[String]] = nice(removeTests(_adapter(dsm), dsm), dsm)
  def _adapter(dsm: DSMDataStructure): Map[String, Entity] = {
    val (sub, sup) = dsm.SPECIALIZE.asEntities.inGroups
    //removeEmptyPockets()
    Map()
  }

}