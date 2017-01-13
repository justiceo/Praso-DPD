package DPD.Browser;

import DPD.Model.DepNode;
import DPD.Model.DependencyType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by Justice on 1/3/2017.
 * This data structure takes a dsm (square matrix) as input and provides an efficient get operation around it
 */
public class DSMDataStructure {

    // Horizontal = Dependencies (files I need)
    // Vertical = Dependents (files that need me)
    protected List<ClassNode> allClassNodes = new ArrayList<>();
    protected HashMap<DependencyType, Integer> dependencyTypes;
    protected int matrixSize = 0;

    public DSMDataStructure(String[] matrix, String[] filePaths, List<DependencyType> dependencies) {
        if(matrix.length != filePaths.length)
            throw new IllegalArgumentException("matrix size must equal number of files");
        matrixSize = matrix.length;
        dependencyTypes = new HashMap<>();
        for(int i = 0; i < dependencies.size(); i++) {
            dependencyTypes.put(dependencies.get(i), i);
        }

        // initialize all nodes
        for(int i = 0; i < matrixSize; i++) {
            allClassNodes.add(new ClassNode(filePaths[i]));
        }

        // start connecting the dots
        for(int row = 0; row < matrixSize; row++) {
            String line = matrix[row].trim();
            String[] cells = line.split(" ");
            if(cells.length != matrixSize)
                throw new IndexOutOfBoundsException("Non-square matrix detected");
            for(int col = 0; col < matrixSize; col++) {
                String data = cells[col];
                if( !data.equals("0") && data.length() != dependencyTypes.size())
                    throw new IllegalStateException("Invalid data length. Number of exhibited dependencies: " + dependencyTypes.size() + ", data length: " + data.length());

                if( data.length() == 1) continue;
                DepNode dn = new DepNode(cells[col], row, col);
                allClassNodes.get(row).row.add(dn);
                allClassNodes.get(col).column.add(dn);
            }
        }
    }

    /**
     * Returns an array of the classes that this indexOfClass depends on
     * Essentially returns the horizontal dependency
     * @param indexOfClass
     * @return
     */
    public int[] getDependencies(int indexOfClass) {
        return getListValues(allClassNodes.get(indexOfClass).row, false);
    }

    /** Same as getDependencies, except for vertical axis */
    public int[] getDependents(int indexOfClass) {
        return getListValues(allClassNodes.get(indexOfClass).column, true);
    }

    /**
     * Get all classes that has the specified type of dependency on this class index
     * @param indexOfClass
     * @param dependencies
     * @return
     */
    public List<Integer> getDependencies(int indexOfClass, DependencyType... dependencies) {
        return getListWhereValues(allClassNodes.get(indexOfClass).row, dependencies, false);
    }

    /** Same as getDependencies except for vertical */
    public List<Integer> getDependents(int indexOfClass, DependencyType... dependencies) {
        return getListWhereValues(allClassNodes.get(indexOfClass).column, dependencies, true);
    }

    public List<Integer> getClassesWithDependencies(DependencyType... dependencies) {
        List<Integer> result = new ArrayList<>();
        for(int i = 0; i < matrixSize; i++) {
            List<Integer> x = getDependencies(i, dependencies);
            result.addAll(x);
        }
        return result;
    }

    public List<Integer> getClassesWithDependents(DependencyType... dependencies) {
        List<Integer> result = new ArrayList<>();
        for(int i = 0; i < matrixSize; i++) {
            List<Integer> x = getDependents(i, dependencies);
            result.addAll(x);
        }
        return result;
    }

    /**
     * Determine if a particular dependency exists on this class' dependency line
     * @param indexOfClass
     * @param indexOfDependency
     * @return
     */
    public boolean hasDependency(int indexOfClass, DependencyType dependency) {
        return hasDependency(allClassNodes.indexOf(indexOfClass), dependency);
    }
    public boolean hasDependency(ClassNode c, DependencyType dependency) {
        int indexOfDependency = dependencyTypes.get(dependency);
        List<DepNode> deps = c.row;
        for(DepNode dep: deps) {
            if(dep.value.charAt(indexOfDependency) == '1')
                return true;
        }
        return false;
    }



    /** Same as hasDependency, excerpt for vertical axis */
    public boolean hasDependent(int indexOfClass, int indexOfDependency) {
        List<DepNode> deps = allClassNodes.get(indexOfClass).column;
        for(DepNode dep: deps) {
            if(dep.value.charAt(indexOfDependency) == '1')
                return true;
        }
        return false;
    }

    public List<DepNode> getDependents(int indexOfClass, int indexOfDependency) {
        List<DepNode> deps = allClassNodes.get(indexOfClass).column;
        List<DepNode> result = new ArrayList<>();
        for(DepNode dep: deps) {
            if(dep.value.charAt(indexOfDependency) == '1')
                result.add(dep);
        }
        return result;
    }

    /**
     * Given an adjacency list or a dependency list, returns all the classes in the list
     * @param list
     * @return
     */
    private int[] getListValues(List<DepNode> list, boolean isRow) {
        int[] result = new int[list.size()];
        for(int i = 0; i < result.length; i++) {
            result[i] = isRow ? list.get(i).row : list.get(i).col;
        }
        return result;
    }

    /**
     * Given an adjacency list, returns all the classes that has a specified dependency
     * @param list
     * @param dependencies
     * @return
     */
    private List<Integer> getListWhereValues(List<DepNode> list, DependencyType[] dependencies, boolean isRow) {
        List<Integer> result = new LinkedList<>();
        int indexOfDependency = dependencyTypes.get(dependencies[0]);
        Logger.getGlobal().warning("only first dep is processed in DSMDataStructure.java:getListWhereValues()");
        for(int i = 0; i < list.size(); i++) {
            DepNode n = list.get(i);
            if( n.value.charAt(indexOfDependency) == '1') {
                int c = isRow ? n.row : n.col;
                result.add(c);
            }
        }
        return result;
    }
    private List<Integer> getListWhereValuesEnum(List<DepNode> list, int dependencies, boolean isRow) {
        List<Integer> result = new LinkedList<>();
        for(int i = 0; i < list.size(); i++) {
            DepNode n = list.get(i);
            if( n.numValue == dependencies) {
                int c = isRow ? n.row : n.col;
                result.add(c);
            }
        }
        return result;
    }

    class ClassNode {
        List<DepNode> column = new ArrayList<>();
        List<DepNode> row = new ArrayList<>();
        String filePath;

        public ClassNode(String path) {
            this.filePath = path;
        }
    }
}
