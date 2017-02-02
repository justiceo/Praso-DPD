package DPD;

import DPD.Model.DependencyType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Justice on 1/5/2017.
 */
public class Util {

    /**
     * Converts the first line in a dsm to a list of dependency types
     *
     * @param dependencyLine
     * @return
     */
    public static List<DependencyType> getDependencyTypes(String dependencyLine) {
        String[] depStrings = extractArray(dependencyLine);
        List<DependencyType> dependencyTypes = new ArrayList<>(depStrings.length);
        for (int i = 0; i < depStrings.length; i++) {
            if( isDependencyCondition(depStrings[i])) {
                DependencyType dependencyType = DependencyType.valueOf(depStrings[i].toUpperCase());
                dependencyTypes.add(dependencyType);
            }
        }
        return dependencyTypes;
    }

    public static boolean isDependencyCondition(String enumStr) {
        for (DependencyType d : DependencyType.values()) {
            if (d.name().equals(enumStr.toUpperCase())) { // todo: testto upper
                return true;
            }
        }
        return false;
    }

    /**
     * Returns all the junk before the source (src) folder
     * @param filePath
     * @return
     */
    public static String getAbsDirFromPath(String filePath) {
        // we'll use "src" for now as root of dev source code
        int cutoff = filePath.indexOf("src");
        if(cutoff != -1) {
            return filePath.substring(0, cutoff);
        }
        return null;
    }

    /**
     * Returns the path of the file starting from the source (src) directory
     * @param absolutePath
     * @return
     */
    public static String getRelativePath(String absolutePath) {
        return absolutePath.replace(getAbsDirFromPath(absolutePath), "");
    }

    /**
     * Converts D:.Code.IdeaProjects.maze.src.edu.drexel.cs.maze.MazeFactory_java
     * to D:/Code/IdeaProjects/maze/src/edu/drexel/cs/maze/MazeFactory.java
     * @param damagedPath
     * @return
     */
    public static String fixFilePath(String damagedPath) {
        try {
            // first confirm its broken
            int l = damagedPath.lastIndexOf("_");
            if (l == -1) return damagedPath; // it's good.

            String ext = damagedPath.substring(l);
            if (!ext.equals("_java")) {
                System.out.println("cannot fix path: " + damagedPath);
                return damagedPath;
            }
            return damagedPath.substring(0, l).replace(".", "\\") + ext.replace("_", ".");

        } catch (IndexOutOfBoundsException e) {
            System.out.println("error converting " + damagedPath);
            return damagedPath;
        }
    }

    public static String getType(String filePath) {
        return filePath.substring(filePath.lastIndexOf("\\")+1, filePath.lastIndexOf("."));
    }

    public static boolean isArray(String args) {
        return args.startsWith("[") && args.endsWith("]");
    }

    public static String[] extractArray(String args){
        if(isArray(args))
            args = args.replace("[", "").replace("]", "");
        String[] arr = args.split(",");
        for(int i = 0; i < arr.length; i++) {
            arr[i] = arr[i].trim();
        }
        return arr;
    }

    public static List<String> extractList(String s) {
        String[] arr = extractArray(s);
        return Arrays.asList(arr);
    }
}
