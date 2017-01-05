package DPD;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Logger;

/**
 * Created by Justice on 3/20/2016.
 * Represents the model of a DSM file
 */
public class DSMDependencyRep implements DependencyRep {

    private boolean isDamaged = true;
    /**
     * Contains a list of the different kinds of dependencies that exist in this matrix
     * E.g. Use, Call, Inherit
     */
    private String exhibitedDependencyLine;

    /**
     * Contains a list of the full paths to the java classes represented in the matrix
     * E.g. D:.Code.IdeaProjects.maze.src.edu.drexel.cs.maze.MazeFactory_java
     */
    private String[] filePaths;

    /**
     * Contains the matrix of the dependencies between the classes
     * E.g. 0 0 0 0 0 0 01100100 0
     */
    private String[] matrixLines;
    private String[] matrixColumns;

    /**
     * The fully qualified filename of the dsm file in question
     */
    private String dsmFilePath;

    /**
     * The root path of the source code, all the junk before before '/src'
     */
    private String absDir;
    private Logger log = Logger.getLogger(getClass().getName().toString());

    public DSMDependencyRep() {
    }

    public DSMDependencyRep(String dsmFilePath) {
        this.dsmFilePath = dsmFilePath;
    }

    public boolean tryLoad() {
       /* tryLoad dsm file */
        Scanner in = null;
        try {
            in = new Scanner(new File(dsmFilePath));
        } catch (FileNotFoundException e) {
            log.severe("Dsm file does not exist: " + dsmFilePath);
            return false;
        }

        exhibitedDependencyLine = in.nextLine();
        int matrixSize = Integer.parseInt(in.nextLine());

        matrixLines = new String[matrixSize];
        for (int i = 0; i < matrixSize; i++) {           /* read the matrix */
            matrixLines[i] = in.nextLine();
        }

        filePaths = new String[matrixSize];
        for (int i = 0; i < matrixSize; i++) {                    /* read the java files */
            filePaths[i] = fixFilePath(in.nextLine());
        }

        // close file input
        in.close();

        // Set the source directory
        absDir = getAbsDirFromPath(filePaths[0]);
        return true;
    }

    public String getExhibitedDependencyLine() {
        return exhibitedDependencyLine;
    }

    public String[] getMatrixLines() {
        return matrixLines;
    }

    public String[] getFilePaths() {
        return filePaths;
    }

    public void saveAs(String fileName, String dependencyLine, String[] filePaths, String[] matrixLines) throws IOException {
        if (filePaths == null || matrixLines == null || dependencyLine == null) {
            System.out.println("the information necessary to create a dsm is incomplete");
            return;
        }

        assert filePaths.length == matrixLines.length;
        int matrixSize = filePaths.length;

        BufferedWriter writer = new BufferedWriter(new FileWriter(new File(fileName)));
        writer.write(dependencyLine + "\n");
        writer.write(matrixSize + "\n");
        for (int i = 0; i < matrixSize; i++) {
            writer.write(matrixLines[i] + "\n");
        }
        for (String filePath : filePaths) {
            writer.write(filePath + "\n");
        }

        writer.flush();
        writer.close();
    }

    public void saveWithRelativePaths() throws IOException {
        if (filePaths == null || matrixLines == null || exhibitedDependencyLine == null) {
            System.out.println("the information necessary to create a dsm is incomplete");
            return;
        }

        int matrixSize = filePaths.length;
        for (int i = 0; i < filePaths.length; i++) {
            filePaths[i] = getRelativePath(filePaths[i]);
        }


        BufferedWriter writer = new BufferedWriter(new FileWriter(new File(dsmFilePath)));
        writer.write(exhibitedDependencyLine + "\n");
        writer.write(matrixSize + "\n");
        for (int i = 0; i < matrixSize; i++) {
            writer.write(matrixLines[i] + "\n");
        }
        for (String filePath : filePaths) {
            writer.write(filePath + "\n");
        }
        writer.write(absDir + "\n");
        writer.flush();
        writer.close();
    }

    /**
     * Returns all the junk before the source (src) folder
     * @param filePath
     * @return
     */
    private String getAbsDirFromPath(String filePath) {
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
    public String getRelativePath(String absolutePath) {
        return absolutePath.replace(absDir, "");
    }

    /**
     * Converts D:.Code.IdeaProjects.maze.src.edu.drexel.cs.maze.MazeFactory_java
     * to D:/Code/IdeaProjects/maze/src/edu/drexel/cs/maze/MazeFactory.java
     * @param damagedPath
     * @return
     */
    public String fixFilePath(String damagedPath) {
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

    public void buildMatrixRowColumns() {
        List<HashMap<String, String>> rowsAdjacencyList;
    }
}
