package DPD;

import java.io.IOException;

/**
 * Created by Justice on 3/20/2016.
 */
public interface DependencyRep {

    String getDependencyLine();

    String[] getMatrixLines();

    String[] getFilePaths();

    void saveAs(String fileName, String dependencyLine, String[] filePaths, String[] matrixLines) throws IOException;
}
