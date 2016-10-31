package DPD;

import com.sun.org.glassfish.gmbal.Description;
import org.apache.commons.lang3.StringUtils;
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
        dsmFile = "files\\dsm\\simpleObserverPattern.dsm";
        dsmModel = new DSMDependencyRep(dsmFile);
    }

    @After
    public void tearDown() {
        dsmModel = null;
    }

    @Test
    @Description("Returns the file paths in the dsm")
    public void getFilePaths() {
        String[] filePath = dsmModel.getFilePaths();
        int expectedCount = 5;

        // confirm the count matches that in file
        Assert.assertEquals("File count should be 5", expectedCount, filePath.length);

        // confirm it's not an array of empty strings
        Assert.assertFalse("File paths cannot be empty", StringUtils.isAnyEmpty(filePath));

        // confirm the last item contains ConcreteObserverA
        Assert.assertTrue("The first file should contain Concrete observer B", filePath[0].contains("ConcreteObserverB"));
    }

    @Test
    @Description("Returns the path of the java file from the src folder")
    public void getRelativePathTest() throws FileNotFoundException {
        String firstFile = dsmModel.getFilePaths()[0];

        // confirm the initial file name is a fully qualified path
        String expected = "D:\\Code\\IdeaProjects\\maze\\src\\edu\\drexel\\cs\\maze\\MazeFactory.java";
        Assert.assertEquals("Original file should match", expected, firstFile);

        // check if relative path starts from src
        expected = "src\\edu\\drexel\\cs\\maze\\MazeFactory.java";
        firstFile = dsmModel.getRelativePath(firstFile);
        Assert.assertEquals("Relative path did not start from src", expected, firstFile);
    }
