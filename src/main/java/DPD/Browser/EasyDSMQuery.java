package DPD.Browser;

import DPD.Model.*;

import java.util.ArrayList;
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
                getSpecializeDependencies(t.X, t.Y);
                break;
            default:
                getPairDependencies(dependency, t.X, t.Y);
                break;
        }
    }

    public void getSpecializeDependencies(Entity xList, Entity yList) {
        int implementIndex = dependencyTypes.containsKey(DependencyType.IMPLEMENT) ? dependencyTypes.get(DependencyType.IMPLEMENT) : 0;
        int extendIndex = dependencyTypes.containsKey(DependencyType.EXTEND) ? dependencyTypes.get(DependencyType.EXTEND) : 0;
        int dependency = implementIndex + extendIndex;

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

    public void getPairDependencies(DependencyType dependency, Entity xList, Entity yList) {
        if( !dependencyTypes.containsKey(dependency) )
            return;
        int indexOfDependency = dependencyTypes.get(dependency);
        for(ClassNode c: allClassNodes) {
            List<DepNode> dependents = c.column;
            for(DepNode dep: dependents) {
                if(dep.value.charAt(indexOfDependency) == '1') {
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
