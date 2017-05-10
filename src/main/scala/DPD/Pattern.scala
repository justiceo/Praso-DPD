package DPD

import DPD.DependencyType._
import DPD.Util.Entity
import DPD.Util._Tuple2

object Pattern {

    def observer(dsm: DSMDataStructure): Map[String, List[Int]] = {
        // observer interfaces must be extended by concrete observers, typed and called by subjects
        val (sup, sub) = dsm.SPECIALIZE.min(2) asEntities
        val obsI = sup.thatIs(List(TYPED, CALL)) // some pockets are removed at this point
        val subj = dsm.classesThat(List(TYPED, CALL), obsI.ids)

        val (conc, subj) = obsI.reconcile(sub, subj)

        val observerInterface: List[Int] = dsm.dependents(EXTEND, TYPED, CALL)
        val concPair: List[(Int, Int)] = dsm.subClasses(observerInterface)
        val subjPair: List[(Int, Int)] = dsm.classesThat(List(TYPED, CALL), observerInterface)

        val (concreteObservers, subjects) = observerInterface.reconcile(concPair, subjPair)

        Map("Observer Interface" -> observerInterface,
            "Concrete Observer" -> concreteObservers,
            "Subject" -> subjects)
    }

    def visitor(dsm: DSMDataStructure): Map[String, List[Int]] = {
        val (sup, sub) = dsm SPECIALIZE
        val pockets: Entity = pocketize(dsm.SPECIALIZE.asEntities)
        val visitorP = pockets.that(List(TYPED, CALL), sub)
        val elementsP = pockets.that(List(TYPED, CALL), sup)
        val concVisitor = dsm.subClasses(visitorP)
        val concEle = dsm.subClasses(elementsP)

        Map("Visitor Interface" -> visitorP,
            "Concrete Visitor" -> concVisitor,
            "Element" -> elementsP,
            "Concrete Element" -> concEle)
    }

    def min_pocket(classes: List[(Int, Int)], i: Int) = ???

    def decorator(dsm: DSMDataStructure): Map[String, List[Int]] = {
        val (sup:List[Int], sub:List[Int]) = min_pocket(dsm.SPECIALIZE, 3)
        val decorator = sup intersect sub
        val concDecorator = dsm subClasses decorator
        val component = dsm superClasses decorator
        val concComponent = dsm.subClasses(component).exclude(decorator, concDecorator)

        Map("Component" -> component,
            "Decorator" -> decorator,
            "Concrete Component" -> concComponent,
            "Concrete Decorator" -> concDecorator)
    }

    def composite(dsm: DSMDataStructure): Map[String, List[Int]] = {
        val (sup, sub) = min_pocket(dsm.SPECIALIZE, 3)
        val composite = sub.classesThat(List(TYPED), sup)
        val component = dsm.superClasses(composite)
        val leaf = sub.exclude(composite)

        //removeEmptyPockets()
        
        Map("Composite" -> composite,
            "Component" -> component,
            "Leaf" -> leaf)
    }    
}