package DPD.DependencyBrowser;

import DPD.Claim;
import DPD.Enums.ClassType;
import DPD.Enums.DependencyType;
import DPD.JClass;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;


/**
 * Created by Justice on 1/27/2016.
 */
public class IDMBrowser implements DSMBrowser {

    private final int matrixSize;
    private final List<DependencyType> dependencyTypes;
    private final List<JClass> jClasses;
    private final Logger logger;
    private final int dependencyTypesSize;

    public IDMBrowser(Logger logger, List<JClass> jClasses, String dependencyLine) {
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
     * @param value
     * @return
     */
    public List<String> getClassesOfType(ClassType classType, String withDependencies, String value) {

        return getClassesOfType(classType, value);
        // implement withDependencies later, cause we may not need it again
        // withDependencies allows us to add small filterings on the entity
    }

    public List<String> getDomDependencies(String classId, DependencyType dependencyType) {
        String depLine = jClasses.stream().filter(c -> c.typeId.equals(classId)).findFirst().get().dependencyLine;
        switch (dependencyType) {
            case SPECIALIZE:
                List<String> x = getIndicesOfDomDependenciesAsClassIds(depLine, DependencyType.EXTEND);
                x.addAll(getIndicesOfDomDependenciesAsClassIds(depLine, DependencyType.IMPLEMENT));
                return x;
            default:
                return getIndicesOfDomDependenciesAsClassIds(depLine, dependencyType);
        }
    }

    // todo: invalid, remove
    public String getType(String typeIdentifier) {
        return getJClassFromTypeId(typeIdentifier).typeId;
    }

    public List<Claim> getClaims(String typeIdentifier) {
        return getJClassFromTypeId(typeIdentifier).claims;
    }

    public int getId(String typeIdentifier) {
        return getJClassFromTypeId(typeIdentifier).classId;
    }

    public JClass getJClassFromTypeId(String typeIdentifier) {
        return jClasses.stream().filter(c -> c.typeId.equals(typeIdentifier)).findFirst().get();
    }

    public String getClassPath(String typeIdentifier) {
        return jClasses.stream().filter(c -> c.typeId.equals(typeIdentifier)).findFirst().get().filePath;
    }

    public void addClaim(String classId, String key, String value) {
        JClass jClass = jClasses.stream().filter(c -> c.typeId.equals(classId)).findFirst().get();
        Claim claim = new Claim(key, value);
        if(jClass.claims == null) jClass.claims = new LinkedList<>();
        jClass.claims.add(claim);
    }

    private List<String> getClassesOfType(ClassType classType, String absoluteValue) {
        switch (classType) {
            case Class:
                return jClasses.stream()
                        .filter(c -> c.classType == ClassType.Class)
                        .map(c -> c.typeId).collect(Collectors.toList());
            case Interface:
                return jClasses.stream()
                        .filter(c -> c.classType == ClassType.Interface)
                        .map(c -> c.typeId).collect(Collectors.toList());
            case Absolute:
                List<String> x = new ArrayList<>();
                x.add(absoluteValue);
                return x;
            case Abstract:
                return jClasses.stream()
                        .filter(c -> c.classType == ClassType.Interface)
                        .map(c -> c.typeId).collect(Collectors.toList());
            case Any:
                return jClasses.stream()
                        .map(j -> j.typeId).collect(Collectors.toList());
            case Abstraction:
                return jClasses.stream()
                        .filter(c -> c.classType == ClassType.Interface || c.classType == ClassType.Abstract)
                        .map(j -> j.typeId).collect(Collectors.toList());
            case Specialization:
                return jClasses.stream()
                        .filter(j -> hasDominantDependency(j.classId, DependencyType.IMPLEMENT) || hasDominantDependency(j.classId, DependencyType.EXTEND))
                        .map(j -> j.typeId).collect(Collectors.toList());
            default: // the other ones we don't really care about (for now)
                return jClasses.stream()
                        .filter(j -> j.classType.equals(classType))
                        .map(j -> j.typeId).collect(Collectors.toList());
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
        while (0 <= depId && depId <= dependencyTypesSize * matrixSize) {
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

    private List<String> getIndicesOfDomDependenciesAsClassIds(String depLine, DependencyType dependencyType) {
        int depId = dependencyTypes.indexOf(dependencyType);
        List<String> indices = new ArrayList<>();

        while (depId < depLine.length() && depId != -1) {
            if (depLine.charAt(depId) == '1') {
                int index = depId / dependencyTypesSize;
                indices.add(jClasses.get(index).typeId);
            }
            depId += dependencyTypesSize;
        }
        return indices;
    }
}
