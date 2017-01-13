package DPD.Model;

import java.io.*;
import java.util.Scanner;

/**
 * Created by Justice on 3/20/2016.
 */
public class IDMFileModel implements FileModel {

    private String dependencyLine;
    private String[] filePaths;
    private String[] matrixLines;

    public IDMFileModel(String fileName) throws FileNotFoundException {
        Scanner in = new Scanner(new File(fileName));          /* load idm file */
        dependencyLine = in.nextLine();
        int matrixSize = Integer.parseInt(in.nextLine());

        matrixLines = new String[matrixSize];
        filePaths = new String[matrixSize];
        for (int i = 0; i < matrixSize; i++) {           /* read the matrix */
            String[] parts = in.nextLine().split(" ::: ");
            filePaths[i] = parts[0];
            matrixLines[i] = parts[1];
        }
        in.close();
    }

    public String getExhibitedDependencyLine() {
        return dependencyLine;
    }

    public String[] getMatrixLines() {
        return matrixLines;
    }

    public String[] getFilePaths() {
        return filePaths;
    }
}
