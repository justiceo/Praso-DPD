package DPD.Model;

import DPD.Util;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.logging.Logger;

/**
 * Created by Justice on 3/20/2016.
 * Represents the model of a DSM file
 */
public class DSMFileModel implements FileModel {

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


    public DSMFileModel(String dsmFilePath) {

        Scanner in = null;
        try {
            in = new Scanner(new File(dsmFilePath));
        } catch (FileNotFoundException e) {
            Logger.getGlobal().severe("Dsm file does not exist: " + dsmFilePath);
            return;
        }

        exhibitedDependencyLine = in.nextLine();
        int matrixSize = Integer.parseInt(in.nextLine());

        matrixLines = new String[matrixSize];
        for (int i = 0; i < matrixSize; i++) {           /* read the matrix */
            matrixLines[i] = in.nextLine();
        }

        filePaths = new String[matrixSize];
        for (int i = 0; i < matrixSize; i++) {                    /* read the java files */
            filePaths[i] = Util.fixFilePath(in.nextLine());
        }

        // close file input
        in.close();
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
}
