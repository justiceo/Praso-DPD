package DPD;

import DPD.Enums.DependencyType;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
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
    DSMDataStructure ds;
    List<DependencyType> dependencyTypes;

    @Before
    public void setup() {
        dependencyTypes = Util.getDependencyTypes("[Typed,Use,Implement]");
        ds = new DSMDataStructure(matrix, matrix, dependencyTypes);
    }

    @Test
    public void getHorizonDepTest() {
        // row 1
        int[] expected = new int[]{1};
        int[] actual = ds.getDependencies(0);
        Assert.assertArrayEquals(expected, actual);

        // row 2
        expected = new int[]{};
        actual = ds.getDependencies(1);
        Assert.assertArrayEquals(expected, actual);

        // row 5
        expected = new int[]{1};
        actual = ds.getDependencies(4);
        Assert.assertArrayEquals(expected, actual);
    }

    @Test
    public void getVerticalDepTest() {

        // col 1
        int[] expected = new int[]{2};
        int[] actual = ds.getDependents(0);
        Assert.assertArrayEquals(expected, actual);

        // col 2
        expected = new int[]{0,4};
        actual = ds.getDependents(1);
        Assert.assertArrayEquals(expected, actual);

        // col 5
        expected = new int[]{};
        actual = ds.getDependents(4);
        Assert.assertArrayEquals(expected, actual);

    }

    @Test
    public void getVerticalDepsWhereTest() {
        List<Integer> expected = new ArrayList<>();
        expected.add(2);
        List<Integer> actual = ds.getDependents(0, DependencyType.TYPED);
        Assert.assertEquals(expected, actual);
    }
    @Test
    public void getHorizonDepWhereTest() {
        List<Integer> expected = new ArrayList<>();
        List<Integer> actual = ds.getDependencies(0, DependencyType.TYPED);
        Assert.assertEquals(expected, actual);
    }
}
