package DPD.Model;

import DPD.Util;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.logging.Logger;

/**
 * Created by Justice on 3/20/2016.
 */
public class DSMModel {

    /**
     * Contains a list of the different kinds of dependencies that exist in this matrix
     * E.g. Use, Call, Inherit
     */
    public String exhibitedDependencyLine;

    /**
     * Contains a list of the full paths to the java classes represented in the matrix
     * E.g. D:.Code.IdeaProjects.maze.src.edu.drexel.cs.maze.MazeFactory_java
     */
    public String[] filePaths;

    /**
     * Contains the matrix of the dependencies between the classes
     * E.g. 0 0 0 0 0 0 01100100 0
     */
    public String[] matrixLines;

    public static DSMModel parse(String dsmFilePath) {
        DSMModel dsmModel = new DSMModel();
        Scanner in = null;
        try {
            in = new Scanner(new File(dsmFilePath));
        } catch (FileNotFoundException e) {
            Logger.getGlobal().severe("Dsm file does not exist: " + dsmFilePath);
            return null;
        }

        dsmModel.exhibitedDependencyLine = in.nextLine();
        int matrixSize = Integer.parseInt(in.nextLine());

        dsmModel.matrixLines = new String[matrixSize];
        for (int i = 0; i < matrixSize; i++) {           /* read the matrix */
            dsmModel.matrixLines[i] = in.nextLine();
        }

        dsmModel.filePaths = new String[matrixSize];
        for (int i = 0; i < matrixSize; i++) {                    /* read the java files */
            dsmModel.filePaths[i] = Util.fixFilePath(in.nextLine());
        }

        // close file input
        in.close();
        return dsmModel;
    }

}
