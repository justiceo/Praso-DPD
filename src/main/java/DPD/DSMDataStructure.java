package DPD;

import java.util.*;

/**
 * Created by Justice on 1/3/2017.
 * This data structure takes a dsm (square matrix) as input and provides an efficient get operation around it
 */
public class DSMDataStructure {

    // Horizontal = Dependencies (files I need)
    // Vertical = Dependents (files that need me)
    private List<ClassNode> allClassNodes = new ArrayList<>();

    public DSMDataStructure(String[] matrix, int dependencyCount) {
        int matrixSize = matrix.length;

        // initialize all nodes
        for(int i = 0; i < matrixSize; i++) {
            allClassNodes.add(new ClassNode());
        }

        // start connecting the dots
        for(int row = 0; row < matrixSize; row++) {
            String line = matrix[row].trim();
            String[] cells = line.split(" ");
            if(cells.length != matrixSize)
                throw new IndexOutOfBoundsException("Non-square matrix detected");
            for(int col = 0; col < matrixSize; col++) {
                String data = cells[col];
                if( !data.equals("0") && data.length() != dependencyCount)
                    throw new IllegalStateException("Invalid data length. Number of exhibited dependencies: " + dependencyCount + ", data length: " + data.length());

                if( data.length() == 1) continue;
                DataNode dn = new DataNode(cells[col], row, col);
                allClassNodes.get(row).row.add(dn);
                allClassNodes.get(col).column.add(dn);
            }
        }
    }

    public DSMDataStructure(Collection<String> matrix, int dependencyCount) {
        this((String[]) matrix.toArray(), dependencyCount);
    }

    /**
     * Returns an array of the classes that this classId depends on
     * Essentially returns the horizontal dependency
     * @param classId
     * @return
     */
    public int[] getHorizontalDep(int classId) {
        return getListValues(allClassNodes.get(classId).row, false);
    }

    /** Same as getHorizontalDep, except for vertical axis */
    public int[] getVerticalDep(int classId) {
        return getListValues(allClassNodes.get(classId).column, true);
    }

    /**
     * Get all classes that has the specified type of dependency on this class index
     * @param indexOfClass
     * @param indexOfDependency
     * @return
     */
    public List<Integer> getHorizontalDepsWhere(int indexOfClass, int indexOfDependency) {
        return getListWhereValues(allClassNodes.get(indexOfClass).row, indexOfDependency, false);
    }

    /** Same as getHorizontalDepsWhere except for vertical */
    public List<Integer> getVerticalDepsWhere(int indexOfClass, int indexOfDependency) {
        return getListWhereValues(allClassNodes.get(indexOfClass).column, indexOfDependency, true);
    }

    /**
     * Determine if a particular dependency exists on this class' dependency line
     * @param classId
     * @param indexOfDependency
     * @return
     */
    public boolean hasOnHorizontalLine(int classId, int indexOfDependency) {
        List<DataNode> deps = allClassNodes.get(classId).row;
        for(DataNode dep: deps) {
            if(dep.value.charAt(indexOfDependency) == '1')
                return true;
        }
        return false;
    }

    /** Same as hasOnHorizontalLine, excerpt for vertical axis */
    public boolean hasOnVerticalLine(int classId, int indexOfDependency) {
        List<DataNode> deps = allClassNodes.get(classId).column;
        for(DataNode dep: deps) {
            if(dep.value.charAt(indexOfDependency) == '1')
                return true;
        }
        return false;
    }

    /**
     * Given an adjacency list or a dependency list, returns all the classes in the list
     * @param list
     * @return
     */
    private int[] getListValues(List<DataNode> list, boolean isRow) {
        int[] result = new int[list.size()];
        for(int i = 0; i < result.length; i++) {
            result[i] = isRow ? list.get(i).row : list.get(i).col;
        }
        return result;
    }

    /**
     * Given an adjacency list, returns all the classes that has a specified dependency
     * @param list
     * @param indexOfDependency
     * @return
     */
    private List<Integer> getListWhereValues(List<DataNode> list, int indexOfDependency, boolean isRow) {
        List<Integer> result = new LinkedList<>();
        for(int i = 0; i < list.size(); i++) {
            DataNode n = list.get(i);
            if( n.value.charAt(indexOfDependency) == '1') {
                int c = isRow ? n.row : n.col;
                result.add(c);
            }
        }
        return result;
    }

    private class ClassNode {
        List<DataNode> column = new ArrayList<>();
        List<DataNode> row = new ArrayList<>();
    }

    private class DataNode {
        int row;
        int col;
        String value;

        public DataNode(String data, int row, int col) {
            this.value = data;
            this.row = row;
            this.col = col;
        }
    }
}
