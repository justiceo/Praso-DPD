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

    boolean hasDependency(String className, DependencyType dependencyType);
    boolean hasDependency(int classId, DependencyType dependencyType);

    ClassType getClassType(String className);
    ClassType getClassType(int classId);

    List<DependencyType> getDependencyTypes();

    /**
     * Given an absolute or relative* class type, it would return list of class names that satisfy this type
     * Absolute class types are Class, Abstract, Interface and relative are Abstraction, Specialization and Any.
     * Specialization is a combo of Any and DependencyType.Extend, DependencyType.Implement
     * Abstraction is a combo of Any and hasDependencyType(Extend), hasDependencyType(Implement).
     * @param classType
     * @return
     */
    List getClassesOfType(ClassType classType);

    List getClassesOfType(ClassType type, String withDependencies);

    boolean isOfClassType(String className, ClassType classType);
    boolean isOfClassType(int classId, ClassType classType);

    List<String> getAssociatedDependency(String fullClassName, DependencyType dependencyType);
    List<String> getAssociatedDependency(int classId, DependencyType dependencyType);

    boolean isAssociatedWithDependency(String testClass, DependencyType implement);
    boolean isAssociatedWithDependency(int classId, DependencyType dependencyType);

    String getNiceName(String fullClassName);
    String getNiceName(int classId);

}


