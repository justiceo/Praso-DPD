package DPD.Model;

import java.io.*;
import java.util.Scanner;

/**
 * Created by Justice on 3/20/2016.
 */
public class IDMFileModel extends FileModel {

    public IDMFileModel(String fileName) {
        super(fileName);
    }

    public void loadFile(String fileName) {
        Scanner in = null;          /* load idm file */
        try {
            in = new Scanner(new File(fileName));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return;
        }
        exhibitedDependencyLine = in.nextLine();
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
}
