package DPD

object Types {

  type Matrix = List[Array[(Int, Int)]]

  case class CNode(classId: Int, score: Int, pocket: Int)

  type Entity = List[CNode]

  implicit class _Matrix(matrix: Matrix) {
    // converts List[Array[(data, colIndex)]] to List[(data, colIndex, rowIndex)]
    lazy val trio: List[(Int, Int, Int)] = matrix.zipWithIndex.flatMap((t) => t._1.map(d => (d._1, d._2, t._2)))
  }

  implicit class _EntityTuple(eTup: (Entity, Entity)) {
    // group hierachies
    def inGroups: (Entity, Entity) = {
      val (sub, sup) = eTup
      var e1: Entity = List.empty
      var e2: Entity = List.empty

      val grouped: Map[Int, Entity] = sup.groupBy(_.classId)

      grouped foreach { case (id, ent) => {
        val otherClasses: Entity = sub.filter(c => ent.pockets.contains(c.pocket))
        val remapped = otherClasses.map(c => CNode(c.classId, 0, ent.pockets.head))
        e2 = e2 ::: remapped
        e1 = CNode(id, 0, ent.pockets.head) :: e1
      }
      }

      (e2, e1)
    }
  }

  implicit class _Entity(entity: Entity) {
    /**
      * returns a disjoint of this entity with the given entities
      */
    def exclude(others: Entity*): Entity = {
      val toExclude = others.flatMap(_.ids).distinct
      entity.filterNot(t => toExclude.contains(t.classId))
    }

    /**
      * returns this entity with members who exhibit a given dependency with classes in another entity
      */
    def that(dep: DependencyType.Value, other: Entity, dsm: DSMDataStructure): Entity = that(List(dep), other, dsm)
    def that(dep1: DependencyType.Value, dep2: DependencyType.Value, other: Entity, dsm: DSMDataStructure): Entity = that(List(dep1, dep2), other, dsm)
    def that(deps: List[DependencyType.Value], other: Entity, dsm: DSMDataStructure): Entity =
      entity.filter(t => !dsm.dependencies(t.classId, deps: _*).intersect(other.ids).isEmpty)

    def thatIs(dep: DependencyType.Value, dsm: DSMDataStructure): Entity = thatIs(List(dep), dsm)
    def thatIs(dep1: DependencyType.Value, dep2: DependencyType.Value, dsm: DSMDataStructure): Entity = thatIs(List(dep1, dep2), dsm)
    def thatIs(deps: List[DependencyType.Value], dsm: DSMDataStructure): Entity =
      entity.filter(t => !dsm.dependents(t.classId, deps: _*).isEmpty)

    def thatIs(dep: DependencyType.Value, other: Entity, dsm: DSMDataStructure): Entity = thatIs(List(dep), other, dsm)
    def thatIs(deps: List[DependencyType.Value], other: Entity, dsm: DSMDataStructure): Entity =
      entity.filter(t => !dsm.dependents(t.classId, deps: _*).intersect(other.ids).isEmpty)

    def reconcile(a: Entity, b: Entity): (Entity, Entity, Entity) = ???

    // if a class in `a` is in `entity`, update `a` pocket to this entity pocket
    def reconcile(a: Entity): Entity = a.map(c => { if (entity.ids.contains(c.classId)) entity.filter(d => d.classId == c.classId).head else c })
    def zipReconcile(a: Entity): Entity = a.zip(entity).map(t => CNode(t._1.classId, t._1.score, t._2.pocket))

    def andIn(other: Entity): Entity = entity.filter(c => other.ids.contains(c.classId))

    /**
      * Returns classes from the given entity with this same pocket as this entity
      * Note: the function name is meant to be "human friendly"
      * The argument to this function is what makes the difference.
      * For super classes, the arg needs to be a SUPER-class entity!!! to get the desired behavior
      * todo: refactor to use dsm
      */
    def superClasses(other: Entity): Entity = other.filter(e => pockets.contains(e.pocket))

    def subClasses(other: Entity): Entity = superClasses(other)

    def ids: List[Int] = entity.map(_.classId)

    def pockets: List[Int] = entity.map(_.pocket)
  }

  implicit class _TupleList(list: List[(Int, Int)]) {
    def asEntities: (Entity, Entity) = {
      val grouped = list.groupBy(_._1).map(t => (t._1, t._2, Util.nextPocket))
      val l1 = grouped.map(t => CNode(t._1, 0, t._3)).toList
      val l2 = grouped.flatMap(t => {
        t._2.map(c => CNode(c._2, 0, t._3))
      }).toList
      (l1, l2)
    }

    def atLeast(n: Int): List[(Int, Int)] = {
      val grouped = list.groupBy(_._2).map(t => (t._1, t._2.size))
      list.filter(t => grouped(t._2) >= n)
    }
  }

}