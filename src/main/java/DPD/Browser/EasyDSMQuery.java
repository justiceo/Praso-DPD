package DPD.Browser;

import DPD.Model.*;

import java.util.Iterator;
import java.util.List;

/**
 * Created by Justice on 1/5/2017.
 */
public class EasyDSMQuery extends DSMDataStructure {

    public EasyDSMQuery(String[] matrix, String[] filePaths, List<DependencyType> dependencyCount) {
        super(matrix, filePaths, dependencyCount);
    }

    public void populate(DependencyType dependency, Tuple t) {
        switch (dependency){
            case SPECIALIZE:
                populate(t, DependencyType.IMPLEMENT, DependencyType.EXTEND);
                break;
            default:
                populate(t, dependency);
                break;
        }
    }

    public void populate(List<DependencyType> dependencies, Tuple t) {
        Iterator<DependencyType> iter = dependencies.iterator();
        while(iter.hasNext()) {
            DependencyType d = iter.next();
            if(d == DependencyType.SPECIALIZE) {
                iter.remove();
                dependencies.add(DependencyType.IMPLEMENT);
                dependencies.add(DependencyType.EXTEND);
            }
        }
        populate(t, (DependencyType[]) dependencies.toArray());
    }


    private void populate(Tuple t, DependencyType... dependencies) {
        int dependency = getDepsAsOne(dependencies);
        Entity xList = t.X;
        Entity yList = t.Y;

        for(ClassNode c: allClassNodes) {
            List<DepNode> dependents = c.column;
            for(DepNode dep: dependents) {
                if((dep.numValue & dependency) == dependency) {
                    CNode ynode;
                    if( yList.hasClass(dep.col) )
                        ynode = yList.getByClassId(dep.col);
                    else {
                        ynode = new CNode(dep.col, Bucket.nextPocket());
                        yList.add(ynode);
                    }
                    CNode xnode = new CNode(dep.row, ynode.pocket);
                    xList.add(xnode);
                }
            }
        }
    }
}
