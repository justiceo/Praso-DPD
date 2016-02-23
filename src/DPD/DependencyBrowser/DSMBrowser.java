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

    private boolean hasAllDependencies(String className, String dependencyLine) {
        List<DependencyType> classDeps = getDependenciesOfClass(className);
        List<DependencyType> desiredDeps = new ArrayList<>();
        Arrays.asList(dependencyLine.split(""))
                .stream().map(s -> DependencyType.valueOf(s.toUpperCase()))
                .forEach(s -> desiredDeps.add(s));
        return classDeps.containsAll(desiredDeps);
    }

    @Override
    public boolean hasDependency(String className, DependencyType dependencyType) {
        List<DependencyType> deps = getDependenciesOfClass(className);
        return deps.contains(dependencyType);
    }

    @Override
    public boolean isAssociatedWithDependency(String fullClassName, DependencyType dependencyType) {
        String depLine = String.join(" ", getMatrixCol(getJClassFromName(fullClassName).matrixIndex));
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
    public List<String> getClassesOfType(ClassType classType) {
        List<String> desiredClasses = new ArrayList<>();
        // dedicating this beautiful line below to choir practice
        if(classType.equals(ClassType.Any))
            return jClasses.stream()
                            .map(j -> j.fileName).collect(Collectors.toList());
        jClasses.stream()
                .filter( j -> j.classType.equals(classType))
                .forEach( j -> desiredClasses.add(j.fileName));

        return desiredClasses;
    }

    @Override
    public List<String> getClassesOfType(ClassType classType, String dependencyLine) {
        if(classType.equals(ClassType.Any))
            return jClasses.stream()
                    .map(j -> j.fileName).collect(Collectors.toList());

        List<String> desiredClasses = new ArrayList<>();
        // dedicating this beautiful line below to choir practice
        jClasses.stream()
                .filter( j -> j.classType.equals(classType))
                .forEach( j -> desiredClasses.add(j.fileName));


        if(dependencyLine != null) {
            List<String> filteredByDependencies = new ArrayList<>();
            for(String desiredClass: desiredClasses) {
                List<DependencyType> dependencyTypes = new LinkedList<>();
                for (String str : dependencyLine.split(",")) {
                    dependencyTypes.add(DependencyType.valueOf(str.toUpperCase()));
                }
                List<DependencyType> classDependencies = getDependenciesOfClass(desiredClass);
                if(classDependencies.containsAll(dependencyTypes))
                    filteredByDependencies.add(desiredClass);
                else if(dependencyTypes.contains(DependencyType.SPECIALIZE)) {
                    // if classDependencies contains all - specialize & extend or implement
                    dependencyTypes.remove(DependencyType.SPECIALIZE);
                    if(dependencyTypes.size() == 0) {
                        if (classDependencies.contains(DependencyType.EXTEND)
                                || classDependencies.contains(DependencyType.IMPLEMENT))
                        {
                            filteredByDependencies.add(desiredClass);
                        }
                    }
                    else if (classDependencies.contains(dependencyTypes)
                            && (classDependencies.contains(DependencyType.EXTEND)
                            || classDependencies.contains(DependencyType.IMPLEMENT)))
                    {
                        filteredByDependencies.add(desiredClass);
                    }
                }
            }
            return filteredByDependencies;
        }
        return desiredClasses;
    }

    @Override
    public boolean isOfClassType(String className, ClassType classType) {
        // not sufficient for Abstraction and Sealed etc. Fine for now.
        return getJClassFromName(className).classType.equals(classType);
    }

    @Override
    public String[][] getDependencyMatrix() {
        return matrix;
    }

    @Override
    public List<String> getFilesList() {
        return files;
    }

    @Override // remove this guy later, lazy code
    public String[] getNiceNames(String[] fullClassNames) {
        String[] niceNames = new String[fullClassNames.length];
        int counter = 0;
        for(String className: fullClassNames) {
            niceNames[counter++] = getNiceName(className);
        }
        return niceNames;
    }

    /**
     * Returns the class that specified one depends on
     * @param fullClassName
     * @param dependencyType
     * @return
     */
    @Override
    public List<String> getAssociatedDependency(String fullClassName, DependencyType dependencyType) {
        JClass jClass = getJClassFromName(fullClassName);
        List<Integer> depLocations = getIndexOfDependency(jClass, dependencyType);
        List<String> desiredDependencies = new ArrayList<>();
        jClasses.stream().filter(j -> depLocations.contains(j.matrixIndex)).forEach(j -> desiredDependencies.add(j.fileName));
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
            jClass.fileName = files.get(i);
            jClass.matrixIndex = i;
            jClass.matrixRow = matrix[i];
            jClass.dependencyLine = String.join(" ", matrix[i]);
            jClass.classType = determineClassType(jClass);
            jClasses.add(jClass);
        }
    }

    private ClassType determineClassType(JClass jClass) {

        String depLine = String.join(" ", getMatrixCol(jClass.matrixIndex));
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
            if(jClass.fileName.equals(fileName))
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
        JClass jClass = getJClassFromName(className);
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

}
