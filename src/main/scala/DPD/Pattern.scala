package DPD

import DPD.DependencyType._
import DPD.Util.Entity
import DPD.Util._TupleList
import scala.language.postfixOps

object Pattern {

    /** things to watch out for
      * - duplicate classes across entities
      * - scoring mechanism for promoting and demoting
      * - write them unit tests!
      * @param dsm
      * @return
      */

    def observer(dsm: DSMDataStructure): Map[String, Entity] = {
        // observer interfaces must be extended by concrete observers, typed and called by subjects
        val (sup:Entity, sub:Entity) = dsm.SPECIALIZE.atLeast(2) asEntities
        val obsI = sup.thatIs(List(TYPED, CALL), dsm) // some pockets are removed at this point
        val (subj, observerI) = dsm.classesThat(List(TYPED, CALL), sup) asEntities

        val (concreteObservers, subjects) = observerI.reconcile(sub, subj)

        Map("Observer Interface" -> observerI,
            "Concrete Observer" -> concreteObservers,
            "Subject" -> subjects)
    }

    def visitor(dsm: DSMDataStructure): Map[String, Entity] = {
        val (sup, sub) = dsm.SPECIALIZE asEntities
        val visitorP = sup.that(List(TYPED, CALL), sub, dsm)
        val elementsP = sup.that(List(TYPED, CALL), sup, dsm)
        val concVisitor = visitorP.subClasses(sub)
        val concEle = elementsP.subClasses(sub)

        Map("Visitor Interface" -> visitorP,
            "Concrete Visitor" -> concVisitor,
            "Element" -> elementsP,
            "Concrete Element" -> concEle)
    }

    def decorator(dsm: DSMDataStructure): Map[String, Entity] = {
        val (sup, sub) =dsm.SPECIALIZE.atLeast(3) asEntities
        val decorator = sup intersect sub
        val concDecorator = decorator subClasses sub
        val component = decorator superClasses sup
        val concComponent = decorator.subClasses(component).exclude(decorator, concDecorator)

        Map("Component" -> component,
            "Decorator" -> decorator,
            "Concrete Component" -> concComponent,
            "Concrete Decorator" -> concDecorator)
    }

    def composite(dsm: DSMDataStructure): Map[String, Entity] = {
        val (sup, sub) = dsm.SPECIALIZE.atLeast(3) asEntities
        val composite = sub.that(List(TYPED), sup, dsm)
        val component = composite superClasses sup
        val leaf = sub exclude composite

        //removeEmptyPockets()
        Map("Composite" -> composite,
            "Component" -> component,
            "Leaf" -> leaf)
    }    
}