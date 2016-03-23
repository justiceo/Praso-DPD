package DPD.DependencyBrowser;

import DPD.Enums.ClassType;
import DPD.Enums.DependencyType;
import DPD.ILogger;
import DPD.JClass;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


/**
 * Created by Justice on 1/27/2016.
 */
public class IDMBrowser implements IBrowser {

    private final int matrixSize;
    private final List<DependencyType> dependencyTypes;
    private final List<JClass> jClasses;
    private final ILogger logger;
    private final int dependencyTypesSize;

    public IDMBrowser(ILogger logger, List<JClass> jClasses, String dependencyLine) {
        this.logger = logger;
        this.jClasses = jClasses;
        this.matrixSize = jClasses.size();
        this.dependencyTypes = getDependencyTypes(dependencyLine);
        dependencyTypesSize = dependencyTypes.size();
    }

    /**
     * Given a class type and a dependency line
     * Returns all classes with these type that meets ALL the dependencies specified
     * Even for relative types, the dependencies would still be processed - so don't include them because you double the work
     *
     * @param classType
     * @param withDependencies
     * @return
     */
    @Override
    public List<Integer> getClassesOfType(ClassType classType, String withDependencies) {
        return getClassesOfType(classType);
        // implement withDependencies later, cause we may not need it again
        // withDependencies allows us to add small filterings on the entity
    }

    @Override
    public List<Integer> getAuxDependencies(int classId, DependencyType dependencyType) {
        switch (dependencyType) {
            case SPECIALIZE:
                List x = getAuxDep(classId, DependencyType.EXTEND);
                x.addAll(getAuxDep(classId, DependencyType.IMPLEMENT));
                return x;
            default:
                return getAuxDep(classId, dependencyType);
        }
    }

    @Override
    public List<Integer> getDomDependencies(int classId, DependencyType dependencyType) {
        String depLine = getClassFromId(classId).dependencyLine;
        return getIndicesOfDomDependenciesAsClassIds(depLine, dependencyType);
    }

    @Override
    public String getClassPath(int classId) {
        return jClasses.get(classId).classPath;
    }

    private List<Integer> getClassesOfType(ClassType classType) {

        switch (classType) {
            case Class:
                return jClasses.stream()
                        .filter(c -> c.classType == ClassType.Class)
                        .map(c -> c.classId).collect(Collectors.toList());
            case Interface:
                return jClasses.stream()
                        .filter(c -> c.classType == ClassType.Interface)
                        .map(c -> c.classId).collect(Collectors.toList());
            case Abstract:
                return jClasses.stream()
                        .filter(c -> c.classType == ClassType.Interface)
                        .map(c -> c.classId).collect(Collectors.toList());
            case Any:
                return jClasses.stream()
                        .map(j -> j.classId).collect(Collectors.toList());
            case Abstraction:
                return jClasses.stream()
                        .filter(c -> c.classType == ClassType.Interface || c.classType == ClassType.Abstract)
                        .map(j -> j.classId).collect(Collectors.toList());
            case Specialization:
                return jClasses.stream()
                        .filter(j -> hasDominantDependency(j.classId, DependencyType.IMPLEMENT) || hasDominantDependency(j.classId, DependencyType.EXTEND))
                        .map(j -> j.classId).collect(Collectors.toList());
            default: // the other ones we don't really care about (for now)
                return jClasses.stream()
                        .filter(j -> j.classType.equals(classType))
                        .map(j -> j.classId).collect(Collectors.toList());
        }
    }

    private List<Integer> getAuxDep(int classId, DependencyType dependencyType) {
        String depColumn = getDepColumn(classId, dependencyType);
        List<Integer> desiredDependencies = new ArrayList<>();
        for (int i = 0; i < depColumn.length(); i++) {
            if (i == '1')
                desiredDependencies.add(i);
        }
        return desiredDependencies;
    }

    private JClass getClassFromId(int classId) {
        for (JClass jClass : jClasses) {
            if (jClass.classId == classId)
                return jClass;
        }
        return null;
    }

    private String getDepColumn(int classId, DependencyType dependencyType) {
        int depId = dependencyTypes.indexOf(dependencyType);
        int absoluteAddress = classId * dependencyTypesSize + depId;
        StringBuilder sb = new StringBuilder();
        for (JClass jClass : jClasses) {
            sb.append(jClass.dependencyLine.charAt(absoluteAddress));
        }
        return sb.toString();
    }

    /**
     * check if the given class has the given dependency on any other class
     *
     * @param classId
     * @param dependencyType
     * @return
     */
    private boolean hasDominantDependency(int classId, DependencyType dependencyType) {
        String depLine = getClassFromId(classId).dependencyLine;
        int depId = dependencyTypes.indexOf(dependencyType);
        while (depId <= dependencyTypesSize * matrixSize) {
            if (depLine.charAt(depId) == '1') {
                return true;
            }
            depId += dependencyTypesSize;
        }
        return false;
    }

    /**
     * Converts the first line in a dsm to a list of dependency types
     *
     * @param dependencyLine
     * @return
     */
    private List<DependencyType> getDependencyTypes(String dependencyLine) {
        dependencyLine = dependencyLine.replace("[", "").replace("]", "");
        String[] depStrings = dependencyLine.split(",");
        List<DependencyType> dependencyTypes = new ArrayList<>(depStrings.length);
        for (int i = 0; i < depStrings.length; i++) {
            DependencyType dependencyType = DependencyType.valueOf(depStrings[i].toUpperCase());
            dependencyTypes.add(dependencyType);
        }
        return dependencyTypes;
    }

    private List<Integer> getIndicesOfDomDependenciesAsClassIds(String depLine, DependencyType dependencyType) {
        int depId = dependencyTypes.indexOf(dependencyType);
        List<Integer> indices = new ArrayList<>();

        while (depId < depLine.length() && depId != -1) {
            if (depLine.charAt(depId) == '1') {
                int index = depId / dependencyTypesSize;
                indices.add(index);
            }
            depId += dependencyTypesSize;
        }
        return indices;
    }
}
