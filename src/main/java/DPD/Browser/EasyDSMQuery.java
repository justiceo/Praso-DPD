package DPD.Browser;

import DPD.Model.CNode;
import DPD.Model.DependencyType;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Justice on 1/5/2017.
 */
public class EasyDSMQuery extends DSMDataStructure {

    public EasyDSMQuery(String[] matrix, String[] filePaths, List<DependencyType> dependencyCount) {
        super(matrix, filePaths, dependencyCount);
    }

    public void populate(DependencyType dependency, List<CNode> xList, List<CNode> yList) {
        this.allClassNodes.forEach(c -> getPairsWithDependency(c, dependency, xList, yList));
    }

    public void getPairsWithDependency(ClassNode c, DependencyType dependency, List<CNode> xList, List<CNode> yList) {
        // get classes with this dependent 'Y', then get their dependents;
        switch (dependency){
            case SPECIALIZE:
                // do something for special case
                break;
            default:
                // do for common dep types
                break;
        }
    }

    public List<Integer> get_abstraction_classes() {
        return classes_with_this_dependents(DependencyType.EXTEND, DependencyType.IMPLEMENT);
    }

    public List<Integer> get_specializations_of(int indexOfClass) {
        return getDependents(indexOfClass, DependencyType.EXTEND, DependencyType.IMPLEMENT);
    }

    public List<Integer> classes_with_this_dependents(DependencyType... dependencies) {
        List<Integer> result = new ArrayList<>();
        List pre = getClassesWithDependents(dependencies);
        result.addAll(pre);
        return result;
    }
}
