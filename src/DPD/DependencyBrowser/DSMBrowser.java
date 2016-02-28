package DPD.DependencyBrowser;

import DPD.Enums.ClassType;
import DPD.Enums.DependencyType;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;


/**
 * Created by Justice on 1/27/2016.
 */
public class DSMBrowser implements IBrowser{

    private int matrixSize;
    private String[][] matrix;
    private List<String> files;
    private List<DependencyType> dependencyTypes;
    private List<JClass> jClasses;

    @Override
    public void init(File dsmFileName) {
        Scanner in = null;
        try {
            in = new Scanner(dsmFileName);          /* load dsm file */
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
                                                    /* read dependency types and matrixSize */
        buildDependencyTypesList(in.nextLine());
        matrixSize = Integer.parseInt(in.nextLine());

        matrix = new String[matrixSize][matrixSize];
        for(int i=0; i<matrixSize; i++) {           /* read the matrix */
            String matrixRow = in.nextLine();
            matrix[i] = matrixRow.split(" ");
        }

        files = new ArrayList<>();
        while(in.hasNext()) {                       /* read the java files */
            files.add(in.nextLine());
        }

        /*todo: move rest of initializations here */
        buildJClasses();                            /* build jClasses for faster search operations */
    }


    @Override
    public boolean hasDependency(String className, DependencyType dependencyType) {
        List<DependencyType> deps = getDependenciesOfClass(className);
        return deps.contains(dependencyType);
    }
    @Override
    public boolean hasDependency(int classId, DependencyType dependencyType) {
        List<DependencyType> deps = getDependenciesOfClass(classId);
        return deps.contains(dependencyType);
    }

    @Override
    public boolean isAssociatedWithDependency(String fullClassName, DependencyType dependencyType) {
        String depLine = String.join(" ", getMatrixCol(getJClassFromName(fullClassName).classId));
        List<DependencyType> classDeps = getLineDependency(depLine);
        return classDeps.contains(dependencyType);
    }

    @Override
    public boolean isAssociatedWithDependency(int classId, DependencyType dependencyType) {
        String depLine = String.join(" ", getMatrixCol(classId));
        List<DependencyType> classDeps = getLineDependency(depLine);
        return classDeps.contains(dependencyType);
    }

    @Override
    public List<DependencyType> getDependencyTypes() {
        return dependencyTypes;
    }

    @Override
    public ClassType getClassType(String className) {
        return getJClassFromName(className).classType;
    }

    @Override
    public ClassType getClassType(int classId) {
        return getJClassFromName(classId).classType;
    }

    @Override
    public List<String> getClassesOfType(ClassType classType) {

        switch (classType) {
            case Any:
                return jClasses.stream()
                        .map(j -> j.classPath).collect(Collectors.toList());
            case Specialization:
                return jClasses.stream()
                        .filter(j -> hasDependency(j.classPath, DependencyType.IMPLEMENT) || hasDependency(j.classPath, DependencyType.EXTEND))
                        .map(j -> j.classPath).collect(Collectors.toList());
            case Abstraction:
                return jClasses.stream()
                        .filter(j -> isAssociatedWithDependency(j.classPath, DependencyType.IMPLEMENT) || isAssociatedWithDependency(j.classPath, DependencyType.EXTEND))
                        .map(j -> j.classPath).collect(Collectors.toList());
            default: // it's an absolute type!
                return jClasses.stream()
                        .filter( j -> j.classType.equals(classType))
                        .map(j -> j.classPath).collect(Collectors.toList());
        }
    }

    /**
     * Given a class type and a dependency line
     * Returns all classes with these type that meets ALL the dependencies specified
     * Even for relative types, the dependencies would still be processed - so don't include them because you double the work
     * @param classType
     * @param dependencyLine
     * @return
     */
    @Override
    public List<String> getClassesOfType(ClassType classType, String dependencyLine) {
        List<String> desiredClasses = getClassesOfType(classType);
        if(dependencyLine == null) return desiredClasses;


        List<DependencyType> dependencyTypes = Arrays.asList(dependencyLine.split(","))
                                                    .stream().map(s -> DependencyType.valueOf(s.toUpperCase()))
                                                    .collect(Collectors.toList());

        for(Iterator<String> iterator = desiredClasses.iterator(); iterator.hasNext();) {
            List<DependencyType> classDependencies = getDependenciesOfClass(iterator.next());
            if(!containsDependencies(classDependencies, dependencyTypes)) {
                iterator.remove();
            }
        }
        return desiredClasses;
    }

    /**
     * Determines if the list of avaialable dependencies contains the required dependencies
     * @param availableDependencies
     * @param requiredDependencies
     * @return
     */
    private boolean containsDependencies(List<DependencyType> availableDependencies, List<DependencyType> requiredDependencies) {
        boolean isContained;
        for(DependencyType req: requiredDependencies){
            switch (req) {
                case SPECIALIZE:
                    isContained = availableDependencies.contains(DependencyType.EXTEND)
                            || availableDependencies.contains(DependencyType.IMPLEMENT);
                    break;
                default:
                    isContained = availableDependencies.contains(req);
            }
            if(!isContained) return isContained;
        }
        return true;
    }

    @Override
    public boolean isOfClassType(String className, ClassType desiredType) {
        return isOfClassType(getClassPathFromId(getClassIdFromPath(className)), desiredType);
    }

    @Override
    public boolean isOfClassType(int classId, ClassType desiredType) {
        switch (desiredType) {
            case Any:
                return true;
            case Specialization:
                return hasDependency(classId, DependencyType.IMPLEMENT)
                        || hasDependency(classId, DependencyType.EXTEND);
            case Abstraction:
                return isAssociatedWithDependency(classId, DependencyType.IMPLEMENT)
                        || isAssociatedWithDependency(classId, DependencyType.EXTEND);
            default:
                return getJClassFromName(classId).classType.equals(desiredType);
        }
    }

    /**
     * Returns the class that specified one depends on
     * Todo: Would not work for relative dependency types - needs refactoring
     * @param fullClassName
     * @param dependencyType
     * @return
     */
    @Override
    public List<String> getAssociatedDependency(String fullClassName, DependencyType dependencyType) {
        return getAssociatedDependency(getClassIdFromPath(fullClassName), dependencyType);
    }

    @Override
    public List<String> getAssociatedDependency(int classId, DependencyType dependencyType) {
        switch (dependencyType) {
            case SPECIALIZE:
                List x = getAssociatedDependencyNative(classId, DependencyType.EXTEND);
                x.addAll(getAssociatedDependencyNative(classId, DependencyType.IMPLEMENT));
                return x;
            default:
                return getAssociatedDependencyNative(classId, dependencyType);
        }
    }

    private List<String> getAssociatedDependencyNative(int classId, DependencyType dependencyType) {
        JClass jClass = getJClassFromName(classId);
        List<Integer> depLocations = getIndexOfDependency(jClass, dependencyType);
        List<String> desiredDependencies = new ArrayList<>();
        jClasses.stream().filter(j -> depLocations.contains(j.classId)).forEach(j -> desiredDependencies.add(j.classPath));
        return desiredDependencies;
    }


    private List<Integer> getIndexOfDependency(JClass jClass, DependencyType dependencyType) {
        String depLine = jClass.dependencyLine;
        String[] atomicDeps = depLine.split(" ");
        List<Integer> result = new LinkedList<>();
        for(int i = 0; i< atomicDeps.length; i++) {
            if(getAtomicDependency(atomicDeps[i]).contains(dependencyType)) {
                result.add(i);
            }
        }
        return result;
    }

    @Override
    public String getNiceName(String fullClassName) {
        int start = fullClassName.lastIndexOf(".");
        int end = fullClassName.lastIndexOf("_");
        return fullClassName.substring(start+1, end);
    }

    @Override
    public String getNiceName(int classId) {
        String fullClassName = getClassPathFromId(classId);
        int start = fullClassName.lastIndexOf(".");
        int end = fullClassName.lastIndexOf("_");
        return fullClassName.substring(start+1, end);
    }

    private void buildDependencyTypesList(String dependencyString) {
        dependencyString = dependencyString.replace("[", "").replace("]", "");
        String[] depStrings = dependencyString.split(",");
        dependencyTypes = new ArrayList<>();
        for(int i=0; i< depStrings.length; i++) {
            DependencyType dependencyType = DependencyType.valueOf(depStrings[i].toUpperCase());
            dependencyTypes.add(dependencyType);
        }
    }

    private void buildJClasses() {
        jClasses = new ArrayList<>();
        for(int i=0; i<files.size(); i++) {
            JClass jClass = new JClass();
            jClass.classPath = files.get(i);
            jClass.classId = i;
            jClass.matrixRow = matrix[i];
            jClass.dependencyLine = String.join(" ", matrix[i]);
            jClass.classType = determineClassType(jClass);
            jClasses.add(jClass);
        }
    }

    private ClassType determineClassType(JClass jClass) {

        String depLine = String.join(" ", getMatrixCol(jClass.classId));
        List<DependencyType> classDeps = getLineDependency(depLine);

        if(classDeps.contains(DependencyType.IMPLEMENT))
            return ClassType.Interface;
        else if(classDeps.contains(DependencyType.EXTEND))
            return ClassType.Abstract;
        else if(classDeps.contains(DependencyType.SPECIALIZE))
            return ClassType.Abstraction;
        else return ClassType.Class;
    }

    private JClass getJClassFromName(String fileName) {
        for(JClass jClass: jClasses) {
            if(jClass.classPath.equals(fileName))
                return jClass;
        }
        return null;
    }

    private JClass getJClassFromName(int classId) {
        for(JClass jClass: jClasses) {
            if(jClass.classId == classId)
                return jClass;
        }
        return null;
    }

    private List<DependencyType> getAtomicDependency(String atomicDependency) {
        List<DependencyType> availableDeps = new ArrayList<>();
        String[] depString = atomicDependency.split("");
        for(int i=0; i< depString.length; i++){
            if(1 == Integer.parseInt(depString[i])) {
                availableDeps.add(dependencyTypes.get(i));
            }
        }
        return availableDeps;
    }

    //todo: now jclass doesn't need the full array anymore
    private List<DependencyType> getLineDependency(String lineOfDependencies) {
        String[] depsArray = lineOfDependencies.split(" ");
        List<DependencyType> dependencyTypes = new ArrayList<>();
        for(String s: depsArray) {
            if(s.equals("0")) continue;
            dependencyTypes.addAll(getAtomicDependency(s));
        }
        return dependencyTypes;
    }

    private List<DependencyType> getDependenciesOfClass(String className) {
        return getDependenciesOfClass(getClassIdFromPath(className));
    }

    private List<DependencyType> getDependenciesOfClass(int classId) {
        JClass jClass = getJClassFromName(classId);
        String[] depsArray = jClass.matrixRow;
        List<DependencyType> dependencyTypes = new ArrayList<>();
        for(String s: depsArray) {
            dependencyTypes.addAll(getAtomicDependency(s));
        }
        return dependencyTypes;
    }

    private String[] getMatrixCol(int col) {
        String[] resultColumn = new String[matrixSize];
        for(int row = 0; row < matrixSize; row++) {
            resultColumn[row] = matrix[row][col];
        }
        return resultColumn;
    }

    private String[] getMatrixRow(int row) {
        String[] resultRow = new String[matrixSize];
        for(int col = 0; col < matrixSize; col++) {
            resultRow[col] = matrix[row][col];
        }
        return resultRow;
    }

    private String getClassPathFromId(int classId) {
        return jClasses.stream().filter(j -> j.classId == classId).findFirst().get().classPath;
    }
    private int getClassIdFromPath(String path) {
        return jClasses.stream().filter(j -> j.classPath.equals(path)).findFirst().get().classId;
    }

}
