package DPD;

import com.sun.org.glassfish.gmbal.Description;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.FileNotFoundException;

/**
 * Created by Justice on 10/31/2016.
 */
public class DSMDependencyRepTest {
    private String dsmFile;
    private DSMDependencyRep dsmModel;

    @Before
    public void setup() throws FileNotFoundException {
        dsmFile = "files\\dsm\\Maze.dsm";
        dsmModel = new DSMDependencyRep(dsmFile);
    }

    @After
    public void tearDown() {
        dsmModel = null;
    }

    @Test
    @Description("Returns the path of the java file from the src folder")
    public void getRelativePathTest() throws FileNotFoundException {
        String firstFile = dsmModel.getFilePaths()[0];

        // confirm the initial file name is a fully qualified path
        String expected = "D:\\Code\\IdeaProjects\\maze\\src\\edu\\drexel\\cs\\maze\\MazeFactory.java";
        Assert.assertEquals(expected, firstFile);

        // check if relative path starts from src
        expected = "src\\edu\\drexel\\cs\\maze\\MazeFactory.java";
        firstFile = dsmModel.getRelativePath(firstFile);
        Assert.assertEquals(expected, firstFile);
    }
}
