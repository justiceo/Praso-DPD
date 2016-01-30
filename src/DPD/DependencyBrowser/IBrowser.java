package DPD.DependencyBrowser;

import java.io.File;
import java.util.List;

/**
 * Created by Justice on 1/26/2016.
 * Exposees methods for manipulating a dsm
 */
public interface IBrowser {

    void init(File dsmFileName);

    boolean hasDependency(String className, DependencyType dependencyType);

    ClassType getClassType(String className);


    List<DependencyType> getDependencyTypes();

    String[][] getDependencyMatrix();

    List<String> getFilesList();

    String[] getNiceNames(String[] testNames);

    List<String> getClassesOfType(ClassType anInterface);

    boolean isOfClassType(String className, ClassType classType);

    List<String> getAssociatedDependency(String fullClassName, DependencyType dependencyType);

    boolean isAssociatedWithDependency(String testClass, DependencyType implement);
}


