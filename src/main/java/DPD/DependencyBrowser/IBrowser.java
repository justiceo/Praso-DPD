package DPD.DependencyBrowser;

import DPD.Enums.ClassType;
import DPD.Enums.DependencyType;

import java.io.File;
import java.util.Collection;
import java.util.List;

/**
 * Created by Justice on 1/26/2016.
 * Exposees methods for manipulating a dsm
 */
public interface IBrowser {

    void init(File dsmFileName);

    List<DependencyType> getDependencyTypes();

    /**
     * Given an absolute or relative* class type, it would return list of class names that satisfy this classType
     * Absolute class types are Class, Abstract, Interface and relative are Abstraction, Specialization and Any.
     * Specialization is a combo of Any and DependencyType.Extend, DependencyType.Implement
     * Abstraction is a combo of Any and hasDependencyType(Extend), hasDependencyType(Implement).
     * @param classType, withDependencies
     * @return
     */
    List<Integer> getClassesOfType(ClassType classType, String withDependencies);

    List<Integer> getAssociatedDependency(int classId, DependencyType dependencyType);

    boolean isAssociatedWithDependency(int classId, DependencyType dependencyType);

    String getNiceName(int classId);

    String getClassPath(int classId);
}


