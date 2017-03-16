package DPD.Browser;

import DPD.Model.DependencyType;

import java.util.*;
import java.util.logging.Logger;

import static DPD.Util.getType;
import static DPD.Util.list;

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
    protected TypeDict typeDict = new TypeDict();
    List<DependencyType> dependencies;

    public DSMDataStructure(String[] matrix, String[] filePaths, List<DependencyType> dependencies) {
        if(matrix.length != filePaths.length)
            throw new IllegalArgumentException("matrix size must equal number of files");
        matrixSize = matrix.length;

        // refactor dependency matrix and dependencies if necessary
        String availableDeps = getAvailableDependencies(matrix, dependencies.size());
        dependencies = refactorDependencies(dependencies, availableDeps);
        matrix = refactorMatrix(matrix, availableDeps);

        this.dependencies = dependencies;
        dependencyTypes = new HashMap<>();
        for(int i = 0; i < dependencies.size(); i++) {
            dependencyTypes.put(dependencies.get(i), i);
        }

        // initialize all nodes and typeDict
        for(int i = 0; i < matrixSize; i++) {
            allClassNodes.add(new ClassNode(filePaths[i]));
            typeDict.put(getType(filePaths[i]), i);
        }

        // start connecting the dots
        for(int row = 0; row < matrixSize; row++) {
            String line = matrix[row].trim();
            String[] cells = line.split(" ");
            if(cells.length != matrixSize)
                throw new NumberFormatException("Non-square matrix detected");
            for(int col = 0; col < matrixSize; col++) {
                String data = cells[col];
                if( !data.equals("0") && data.length() != dependencyTypes.size())
                    throw new IllegalStateException("Invalid data length. Number of exhibited dependencies: " + dependencyTypes.size() + ", data length: " + data.length());

                if( data.equals("0")) continue;
                DepNode dn = new DepNode(cells[col], row, col);
                allClassNodes.get(row).row.add(dn);
                allClassNodes.get(col).column.add(dn);
            }
        }
    }

    private String getAvailableDependencies(String[] matrix, int size) {
        int num = 0;
        for(String r: matrix) {
            for(String c: r.split(" ")) {
                num = num | Integer.parseInt(c.trim(), 2);
            }
        }
        // add front zeros
        String str = Integer.toBinaryString(num);
        while(str.length() < size)
            str = "0" + str;

        return str;
    }

    // returns all the used dependencies from the old one
    private List<DependencyType> refactorDependencies(List<DependencyType> dependencies, String available) {
        List<DependencyType> result = new ArrayList<>();
        for(int i = 0; i < available.length(); i++) {
            if(available.charAt(i) == '1')
                result.add(dependencies.get(i));
        }
        return result;
    }

    private String[] refactorMatrix(String[] matrix, String available) {
        List<Integer> indices = new ArrayList<>();
        for(int i = 0; i < available.length(); i++) {
            if(available.charAt(i) == '1')
                indices.add(i);
        }
        String[] result = new String[matrix.length];
        for(int i = 0; i < matrix.length; i++) {
            String[] cells = matrix[i].split(" ");
            for(int j = 0; j < cells.length; j++) {
                if(!cells[j].equals("0"))
                    cells[j] = resize(cells[j], indices);
            }
            result[i] = String.join(" ", cells);
        }
        return result;
    }

    private String resize(String original, List<Integer> available) {
        StringBuilder sb = new StringBuilder();
        for(int i: available) {
            sb.append(original.charAt(i));
        }
        return sb.toString();
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
     * @param dependency
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

    int getDepsAsOne(DependencyType... deps) {
        int[] nums = new int[dependencyTypes.size()];
        for(DependencyType d: deps) {
            if(dependencyTypes.containsKey(d))
                nums[dependencyTypes.get(d)] = 1;
        }
        StringBuilder sb = new StringBuilder();
        for(int i: nums)
            sb.append(i);
        return Integer.parseInt(sb.toString(), 2);
    }

    public DSMDataStructure getSubDSM(String type) {
        int[] n = new int[]{typeDict.getClassIndex(type)};
        return getSubDSM(n);
    }

    public DSMDataStructure getSubDSM(String[] args) {
        int[] arr = new int[args.length];
        int i = 0;
        for(String s: args) arr[i++] = typeDict.getClassIndex(s);
        return getSubDSM(arr);
    }

    public DSMDataStructure getSubDSM(int classId) {
        ClassNode cn = allClassNodes.get(classId);
        HashSet<Integer> classSet = new HashSet<>();
        if(cn.row.size() > 0)
            classSet.add(cn.row.get(0).row);
        else if(cn.column.size() > 0)
            classSet.add(cn.column.get(0).col);

        for(DepNode dn: cn.row) { // get dependencies
            classSet.add(dn.col);
        }
        for(DepNode dn: cn.column) { // get dependents
            classSet.add(dn.row);
        }

        List<Integer> classIds = list(classSet);
        Collections.sort(classIds);

        // fill in the file paths
        List<String> filePaths = new ArrayList();
        for(int i: classIds) {
            filePaths.add( allClassNodes.get(i).filePath );
        }

        // create matrix
        int size = classIds.size();
        String[] matrix = new String[size];

        // fill in deps in adjusted space
        for(int index = 0; index < classIds.size(); index++) {
            int id = classIds.get(index);
            String row = expandRowAdj(allClassNodes.get(id).row, classIds);
            matrix[index] = row;
        }

        String[] filePathsArr = new String[filePaths.size()];
        filePaths.toArray(filePathsArr);
        return new DSMDataStructure(matrix, filePathsArr, list(dependencyTypes.keySet()));
    }

    public DSMDataStructure getSubDSM(int... classIds) {
        HashSet<Integer> classSet = new HashSet<>();
        for(int c: classIds) {
            Arrays.stream(getDependencies(c)).forEach(i -> classSet.add(i));
            Arrays.stream(getDependents(c)).forEach(i -> classSet.add(i));
            classSet.add(c);
        }

        List<Integer> sortedClassIds = list(classSet);
        Collections.sort(sortedClassIds);

        // fill in the file paths
        List<String> filePaths = new ArrayList();
        for(int i: sortedClassIds) {
            filePaths.add( allClassNodes.get(i).filePath );
        }

        // create matrix
        int size = sortedClassIds.size();
        String[] matrix = new String[size];

        // fill in deps in adjusted space
        for(int index = 0; index < sortedClassIds.size(); index++) {
            int id = sortedClassIds.get(index);
            String row = expandRowAdj(allClassNodes.get(id).row, sortedClassIds);
            matrix[index] = row;
        }

        String[] filePathsArr = new String[filePaths.size()];
        filePaths.toArray(filePathsArr);
        return new DSMDataStructure(matrix, filePathsArr, dependencies);
    }

    private String expandRowAdj(List<DepNode> row, List<Integer> classIds) {
        StringBuilder res = new StringBuilder();
        for(int classId: classIds) {
            DepNode dn = row.stream().filter(d -> d.col == classId).findFirst().orElse(null);
            res.append(dn == null ? "0 " : dn.value + " ");
        }
        return res.toString();
    }

    public String expandRow(List<DepNode> row, int size) {
        StringBuilder res = new StringBuilder();
        int counter = 0;
        for(DepNode dn: row){
            while(counter++ < dn.col) res.append("0 ");
            res.append(dn.value + " ");
        }
        while(counter++ < size) res.append("0 ");
        return res.toString();
    }

    public List<String> getTypes() {
        return new ArrayList<String>(typeDict.keySet());
    }


    @Override
    public String toString() {
        StringBuilder res = new StringBuilder();
        int size = allClassNodes.size();

        // add dependency types and matrix size
        res.append(dependencyTypes.keySet().toString() + "\n" + size + "\n");

        // add the matrix rows
        for(ClassNode cn: allClassNodes) {
            res.append(expandRow(cn.row, size) + "\n");
        }

        // add the file paths
        allClassNodes.forEach(cn -> res.append(cn.filePath + "\n"));

        return res.toString();
    }

    class ClassNode {
        List<DepNode> column = new ArrayList<>();
        List<DepNode> row = new ArrayList<>();
        String filePath;

        public ClassNode(String path) {
            this.filePath = path;
        }
    }

    class DepNode {
        public int row; // classId on dependency row
        public int col; // classId on dependent row
        public String value;
        public int numValue;

        public DepNode(String data, int row, int col) {
            this.value = data;
            this.row = row;
            this.col = col;
            this.numValue = Integer.parseInt(this.value, 2);
        }

        @Override
        public String toString() {
            return "(" + row + "," + col + "," + value + ")";
        }
    }

    class TypeDict extends HashMap<String, Integer> {
        int getClassIndex(String type) {
            return get(type);
        }
    }
}
