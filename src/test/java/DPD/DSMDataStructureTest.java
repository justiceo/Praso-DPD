package DPD;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Justice on 1/3/2017.
 */
public class DSMDataStructureTest {

    String[] matrix = new String[] {
            "0 001 0 0 0 ",
            "0 0 0 0 0 ",
            "110 0 0 0 0",
            "0 0 0 010 0",
            "0 001 0 0 0"
    };
    int dependencyCount = 3;

    @Test
    public void constructorTest() {
        DSMDataStructure ds = new DSMDataStructure(matrix, dependencyCount);
    }

    @Test
    public void constructorListTest() {
        List<String> ls = Arrays.asList(matrix);
        DSMDataStructure ds = new DSMDataStructure(ls, dependencyCount);
    }

    @Test(expected = IllegalStateException.class)
    public void constructorBadDepCount() {
        List<String> ls = Arrays.asList(matrix);
        DSMDataStructure ds = new DSMDataStructure(ls, dependencyCount + 1);
    }

    @Test
    public void getHorizonDepTest() {
        DSMDataStructure ds = new DSMDataStructure(matrix, dependencyCount);

        // row 1
        int[] expected = new int[]{1};
        int[] actual = ds.getHorizontalDep(0);
        Assert.assertArrayEquals(expected, actual);

        // row 2
        expected = new int[]{};
        actual = ds.getHorizontalDep(1);
        Assert.assertArrayEquals(expected, actual);

        // row 5
        expected = new int[]{1};
        actual = ds.getHorizontalDep(4);
        Assert.assertArrayEquals(expected, actual);
    }

    @Test
    public void getVerticalDepTest() {
        DSMDataStructure ds = new DSMDataStructure(matrix, dependencyCount);

        // col 1
        int[] expected = new int[]{2};
        int[] actual = ds.getVerticalDep(0);
        Assert.assertArrayEquals(expected, actual);

        // col 2
        expected = new int[]{0,4};
        actual = ds.getVerticalDep(1);
        Assert.assertArrayEquals(expected, actual);

        // col 5
        expected = new int[]{};
        actual = ds.getVerticalDep(4);
        Assert.assertArrayEquals(expected, actual);

    }

    @Test
    public void getVerticalDepsWhereTest() {
        DSMDataStructure ds = new DSMDataStructure(matrix, dependencyCount);
        List<Integer> expected = new ArrayList<>();
        expected.add(2);
        List<Integer> actual = ds.getVerticalDepsWhere(0, 0);
        Assert.assertEquals(expected, actual);
    }
    @Test
    public void getHorizonDepWhereTest() {
        DSMDataStructure ds = new DSMDataStructure(matrix, dependencyCount);
        List<Integer> expected = new ArrayList<>();
        List<Integer> actual = ds.getHorizontalDepsWhere(0, 0);
        Assert.assertEquals(expected, actual);
    }
}
