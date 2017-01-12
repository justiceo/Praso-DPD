package DPD;

import DPD.DSMMapper.DepNode;
import DPD.Enums.DependencyType;

import java.util.List;

/**
 * Created by Justice on 1/12/2017.
 */
public interface DSMQuery {
    boolean hasDependency(DepNode dn, DependencyType dependency, List<DepNode> alternate);

    List<DepNode> filter(DependencyType dependency, List<DepNode> alternate);

    List<DepNode> allClasses();
}
