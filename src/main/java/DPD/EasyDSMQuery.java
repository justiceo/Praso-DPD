package DPD;

import DPD.Enums.DependencyType;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Justice on 1/5/2017.
 */
public class EasyDSMQuery extends DSMDataStructure {

    public EasyDSMQuery(String[] matrix, String[] filePaths, List<DependencyType> dependencyCount) {
        super(matrix, filePaths, dependencyCount);
    }

    public List<Integer> get_abstraction_classes() {
        return classes_with_this_dependents(DependencyType.EXTEND, DependencyType.IMPLEMENT);
    }

    public List<Integer> get_specializations_of(int indexOfClass) {
        return getDependents(indexOfClass, DependencyType.EXTEND, DependencyType.IMPLEMENT);
    }

    public List<Integer> classes_with_this_dependents(DependencyType... dependencies) {
        List<Integer> result = new ArrayList<>();
        List pre = getClassesWithDepents(dependencies);
        result.addAll(pre);
        return result;
    }
}
