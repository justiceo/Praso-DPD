package DPD;

import DPD.Enums.DependencyType;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Justice on 1/5/2017.
 */
public class Util {

    /**
     * Converts the first line in a dsm to a list of dependency types
     *
     * @param dependencyLine
     * @return
     */
    public static List<DependencyType> getDependencyTypes(String dependencyLine) {
        dependencyLine = dependencyLine.replace("[", "").replace("]", "");
        String[] depStrings = dependencyLine.split(",");
        List<DependencyType> dependencyTypes = new ArrayList<>(depStrings.length);
        for (int i = 0; i < depStrings.length; i++) {
            DependencyType dependencyType = DependencyType.valueOf(depStrings[i].toUpperCase());
            dependencyTypes.add(dependencyType);
        }
        return dependencyTypes;
    }
}
