package DPD;

import java.io.*;
import java.util.Scanner;

/**
 * Created by Justice on 3/20/2016.
 */
public class DSMDependencyRep implements DependencyRep {

    private String dependencyLine;
    private String[] filePaths;
    private String[] matrixLines;

    public DSMDependencyRep() {}

    public DSMDependencyRep(String fileName) throws FileNotFoundException {

        Scanner in = new Scanner(new File(fileName));          /* load dsm file */
        dependencyLine = in.nextLine();
        int matrixSize = Integer.parseInt(in.nextLine());

        matrixLines = new String[matrixSize];
        for(int i=0; i<matrixSize; i++) {           /* read the matrix */
            matrixLines[i] = in.nextLine();
        }

        filePaths = new String[matrixSize];
        for(int i=0; i<matrixSize; i++) {                    /* read the java files */
            filePaths[i] = in.nextLine();
        }
    }

    public String getDependencyLine() {
        return dependencyLine;
    }

    public String[] getMatrixLines() {
        return matrixLines;
    }

    public String[] getFilePaths() {
        return filePaths;
    }

    public void saveAs(String fileName, String dependencyLine, String[] filePaths, String[] matrixLines) throws IOException {
        if(filePaths == null || matrixLines == null || dependencyLine == null) {
            System.out.println("the information necessary to create a dsm is incomplete");
            return;
        }

        assert filePaths.length == matrixLines.length;
        int matrixSize = filePaths.length;

        BufferedWriter writer = new BufferedWriter(new FileWriter(new File(fileName)));
        writer.write(dependencyLine + "\n");
        writer.write(matrixSize + "\n");
        for(int i = 0; i < matrixSize; i++) {
            writer.write(matrixLines[i] + "\n");
        }
        for(int i = 0; i < matrixSize; i++) {
            writer.write(filePaths[i] + "\n");
        }
        writer.flush();
        writer.close();
    }
}
