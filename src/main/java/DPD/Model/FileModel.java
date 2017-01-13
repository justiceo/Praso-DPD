package DPD.Model;

/**
 * Created by Justice on 3/20/2016.
 */
public abstract class FileModel {

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

    public FileModel(String fileName) {
        loadFile(fileName);
    }

    public abstract void loadFile(String fileName);

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
