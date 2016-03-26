package DPD.DependencyBrowser;

import DPD.Claim;
import DPD.Enums.ClassType;
import DPD.Enums.DependencyType;

import java.util.List;

/**
 * Created by Justice on 1/26/2016.
 * Exposees methods for manipulating a dsm
 */
public interface IBrowser {

    /**
     * Given an absolute or relative* class type, it would return list of class names that satisfy this classType
     * Absolute class types are Class, Abstract, Interface and relative are Abstraction, Specialization and Any.
     * Specialization is a combo of Any and DependencyType.Extend, DependencyType.Implement
     * Abstraction is a combo of Any and hasDependencyType(Extend), hasDependencyType(Implement).
     *
     * @param classType, withDependencies
     * @param value
     * @return
     */
    List<Integer> getClassesOfType(ClassType classType, String withDependencies, String value);

    String getClassPath(int classId);

    List<Integer> getDomDependencies(int classId, DependencyType dependencyType);

    String getType(int targetClassId);

    void addClaim(int classId, String key, String value);

    List<Claim> getClaims(int classId);
}


