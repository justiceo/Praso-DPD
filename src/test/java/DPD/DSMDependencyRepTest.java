package DPD;

import DPD.Model.DSMFileModel;
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
    private DSMFileModel dsmModel;

    @Before
    public void setup() throws FileNotFoundException {
        dsmFile = "files\\dsm\\simpleObserverPattern.dsm";
        dsmModel = new DSMFileModel(dsmFile);
    }

    @After
    public void tearDown() {
        dsmModel = null;
    }

    @Test
    @Description("Returns the file paths in the dsm")
    public void getFilePathsTest() {
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
    @Description("Returns the matrix lines in the dsm")
    public void getMatrixTest() {
        String[] matrixLines = dsmModel.getMatrixLines();
        int expectedCount = 5;

        // confirm the count matches that in file
        Assert.assertEquals("The matrix count should be 5", expectedCount, matrixLines.length);

        // confirm it's not an array of empty strings
        Assert.assertFalse("Matrix lines cannot be empty", StringUtils.isAnyEmpty(matrixLines));

        // confirm the last item contains ConcreteObserverA
        Assert.assertTrue("The first line should contain the dependency", matrixLines[0].contains("001"));
    }


}
