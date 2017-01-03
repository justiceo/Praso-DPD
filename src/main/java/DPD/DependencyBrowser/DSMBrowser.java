package DPD.DependencyBrowser;

import DPD.Claim;
import DPD.Enums.ClassType;
import DPD.Enums.DependencyType;

import java.util.List;

/**
 * Created by Justice on 1/26/2016.
 * Exposees methods for manipulating a dsm
 */
public interface DSMBrowser {

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
    List<String> getClassesOfType(ClassType classType, String withDependencies, String value);

    String getClassPath(String classId);

    List<String> getDomDependencies(String classId, DependencyType dependencyType);

    String getType(String targetClassId);

    void addClaim(String classId, String key, String value);

    List<Claim> getClaims(String classId);

    int getId(String classId);
}


