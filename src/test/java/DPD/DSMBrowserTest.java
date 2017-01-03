package DPD;

import DPD.DependencyBrowser.DSMBrowser;
import DPD.DependencyBrowser.IDMBrowser;
import DPD.Enums.ClassType;
import DPD.Enums.DependencyType;
import DPD.PreProcessor.LoadDSM;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Justice on 1/27/2016.
 */

public class DSMBrowserTest {

    private DSMBrowser browser;
    private final String testDsmFile = "files\\dsm\\simpleObserverPattern.dsm";

    @Before
    public void setup() throws FileNotFoundException, InterruptedException {

        LoadDSM preprocessor = new LoadDSM();
        if (!preprocessor.load(testDsmFile))
            throw new FileNotFoundException();

        preprocessor.buildJClasses();
        browser = new IDMBrowser(new ConsoleLogger(), preprocessor.getClassList(), preprocessor.getDependencyLine());
    }

    @After
    public void tearDown() {
        browser = null;
    }

    @Test
    public void getDependencyTypesTest() {
        List<DependencyType> expectedDependencyTypes = new ArrayList<>();
        expectedDependencyTypes.add(DependencyType.TYPED);
        expectedDependencyTypes.add(DependencyType.IMPLEMENT);
        expectedDependencyTypes.add(DependencyType.USE);

        List<DependencyType> actualDependencyTypes = new ArrayList<>();
        //Assert.assertEquals(expectedDependencyTypes, actualDependencyTypes);
    }

    @Test
    public void getDependencyMatrixTest() {
        String[][] matrix = new String[5][5];
        String[] row1 = new String[]{"0", "0", "0", "0", "010"};
        String[] row2 = new String[]{"0", "0", "0", "0", "101"};
        String[] row3 = new String[]{"0", "0", "0", "0", "010"};
        String[] row4 = new String[]{"0", "0", "0", "0", "101"};
        String[] row5 = new String[]{"0", "0", "0", "0", "0"};
        matrix[0] = row1; matrix[1] = row2; matrix[2] = row3;
        matrix[3] = row4; matrix[4] = row5;

        //String[][] actualMatrix = browser.getDependencyMatrix();
        //assertArrayEquals(matrix, actualMatrix);
    }

    @Test
    public void getFilesListTest() {
        String[] expectedFiles = new String[] {
                "D:.Code.IdeaProjects.DesignPatterns.src.observer.ConcreteObserverA_java",
                "D:.Code.IdeaProjects.DesignPatterns.src.observer.SubjectB_java",
                "D:.Code.IdeaProjects.DesignPatterns.src.observer.ConcreteObserverB_java",
                "D:.Code.IdeaProjects.DesignPatterns.src.observer.SubjectA_java",
                "D:.Code.IdeaProjects.DesignPatterns.src.observer.IObserver_java"
                };

        //List<String> actualFilesList = browser.getFilesList();
        //assertEquals(Arrays.asList(expectedFiles), actualFilesList);
    }

    @Test
    public void getNiceNameTest() {
        String[] testNames = new String[] {
                "D:.Code.IdeaProjects.DesignPatterns.src.observer.ConcreteObserverA_java",
                "D:.Code.IdeaProjects.DesignPatterns.src.observer.SubjectB_java",
                "D:.Code.IdeaProjects.DesignPatterns.src.observer.ConcreteObserverB_java",
                "D:.Code.IdeaProjects.DesignPatterns.src.observer.SubjectA_java",
                "D:.Code.IdeaProjects.DesignPatterns.src.observer.IObserver_java"
        };

        String[] expectedNames = new String[] {
                "ConcreteObserverA",
                "SubjectB",
                "ConcreteObserverB",
                "SubjectA",
                "IObserver"
        };

        //String[] actualNames = browser.getNiceNames(testNames);
        //assertArrayEquals(expectedNames, actualNames);
    }

    @Test
    public void getInterfacesTest() {
        // returns a list of classes that are interfaces.
        String[] expectedFiles = new String[] {
                "D:.Code.IdeaProjects.DesignPatterns.src.observer.IObserver_java"
        };

        //List<String> actualFilesList = browser.getClassesOfType(Interface);
        //assertEquals(Arrays.asList(expectedFiles), actualFilesList);
    }

    public void getSpecializers() {


    }

    public void getClassesOfTypeTest() {
        // merge the others into here
    }

    @Test
    public void getConcreteClassesTest() {
        // returns a list of classes that are concrete.
        String[] expectedFiles = new String[] {
                "D:.Code.IdeaProjects.DesignPatterns.src.observer.ConcreteObserverA_java",
                "D:.Code.IdeaProjects.DesignPatterns.src.observer.SubjectB_java",
                "D:.Code.IdeaProjects.DesignPatterns.src.observer.ConcreteObserverB_java",
                "D:.Code.IdeaProjects.DesignPatterns.src.observer.SubjectA_java"
        };

        //List<String> actualFilesList = browser.getClassesOfType(Class);
        //assertEquals(Arrays.asList(expectedFiles), actualFilesList);

        //Todo: Add tests for rest of ClassType
    }

    @Test
    public void getClassTypeTest() {
        String className = "D:.Code.IdeaProjects.DesignPatterns.src.observer.SubjectA_java";
        ClassType expectedType = ClassType.Class;
        //ClassType actualType = browser.getClassType(className);
        //assertEquals(expectedType, actualType);

        //Todo: Add tests for rest of ClassType
    }

    @Test
    public void isOfClassTypeTest() {
        // determines if a class belongs to a class type.
        String className = "D:.Code.IdeaProjects.DesignPatterns.src.observer.SubjectA_java";
        //boolean isOfType = browser.isOfClassType(className, ClassType.Class);
        //assertTrue(isOfType);
        //isOfType = browser.isOfClassType(className, ClassType.Interface);
        //assertFalse(isOfType);
    }

    @Test
    public void TestGetAssociatedDependencies() {
        String sourceClass = "D:.Code.IdeaProjects.DesignPatterns.src.observer.ConcreteObserverA_java";
        String expectedResult = "D:.Code.IdeaProjects.DesignPatterns.src.observer.IObserver_java";
        /*List<String> actualResult = browser.getAuxDependencies(sourceClass, DependencyType.IMPLEMENT);
        assertEquals(1, actualResult.size());
        assertTrue(actualResult.contains(expectedResult));
        */
    }

    @Test
    public void TestHasDependencyType() {
        String testClass = "D:.Code.IdeaProjects.DesignPatterns.src.observer.SubjectA_java";
        //assertTrue(browser.hasDependency(testClass, DependencyType.TYPED));
        //assertTrue(browser.hasDependency(testClass, DependencyType.USE));
    }

    @Test
    public void TestIsAssociatedWithDependencyType() {
        String testClass = "D:.Code.IdeaProjects.DesignPatterns.src.observer.IObserver_java";
        /*assertTrue(browser.hasAuxiliaryDependency(testClass, DependencyType.TYPED));
        assertTrue(browser.hasAuxiliaryDependency(testClass, DependencyType.USE));
        assertTrue(browser.hasAuxiliaryDependency(testClass, DependencyType.IMPLEMENT));*/
    }
}
