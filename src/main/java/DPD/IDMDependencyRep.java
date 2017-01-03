package DPD;

import java.io.*;
import java.util.Scanner;

/**
 * Created by Justice on 3/20/2016.
 */
public class IDMDependencyRep implements DependencyRep {

    private String dependencyLine;
    private String[] filePaths;
    private String[] matrixLines;

    public IDMDependencyRep() {
    }

    public IDMDependencyRep(String fileName) throws FileNotFoundException {
        Scanner in = new Scanner(new File(fileName));          /* load idm file */
        dependencyLine = in.nextLine();
        int matrixSize = Integer.parseInt(in.nextLine());

        matrixLines = new String[matrixSize];
        filePaths = new String[matrixSize];
        for (int i = 0; i < matrixSize; i++) {           /* read the matrix */
            String[] parts = in.nextLine().split(" ::: ");
            filePaths[i] = parts[0];
            matrixLines[0] = parts[1];
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

    public void saveAs(String fileName, String dependencyLine, String[] filePaths, String[] matrixLines) throws IOException {
        if (filePaths == null || matrixLines == null || dependencyLine == null) {
            System.out.println("the information necessary to create idm is incomplete");
            return;
        }

        assert filePaths.length == matrixLines.length;
        int matrixSize = filePaths.length;

        BufferedWriter writer = new BufferedWriter(new FileWriter(new File(fileName)));
        writer.write(dependencyLine + "\n");
        writer.write(matrixSize + "\n");
        for (int i = 0; i < matrixSize; i++) {
            writer.write(filePaths[i] + " ::: " + matrixLines[i] + "\n");
        }
        writer.flush();
        writer.close();
    }
}
