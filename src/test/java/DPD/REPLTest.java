package DPD;

import DPD.Browser.EasyDSMQuery;
import DPD.Model.DSMFileModel;
import DPD.Model.DependencyType;
import DPD.Model.Entity;
import DPD.REPL.REPL;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

/**
 * Created by Justice on 1/10/2017.
 */
public class REPLTest {

    REPL repl;
    EasyDSMQuery dsmBrowser;
    String testDsmFile = "files/test.dsm";

    @Before
    public void setup() {
        DSMFileModel dsm = new DSMFileModel(testDsmFile);
        List<DependencyType> dependencyTypeList = Util.getDependencyTypes(dsm.exhibitedDependencyLine);
        dsmBrowser = new EasyDSMQuery(dsm.matrixLines, dsm.filePaths, dependencyTypeList);
        repl = new REPL(dsmBrowser);
    }

    @After
    public void tearDown() {
        repl = null;
        dsmBrowser = null;
    }

    @Test
    public void testDeclarationStmts() throws Exception {
        // get accessible exec environment
        ExecEnvAccess exec = new ExecEnvAccess(repl);

        // at the start it should be empty
        Assert.assertTrue(exec.bucketList.keySet().isEmpty());
        Assert.assertTrue(exec.declaredVariables.isEmpty());

        // after execution, declaredVariables should contain 1 item
        String line = "Entity observerI: 'Observer Interface'";
        repl.execute(line);
        Assert.assertTrue(exec.bucketList.keySet().isEmpty());
        Assert.assertTrue(exec.declaredVariables.size() == 1);

        // let's add a bucket to the mix
        line = "Bucket b: 'Simple Observer Pattern'";
        repl.execute(line);
        Assert.assertTrue(exec.bucketList.keySet().size() == 1);
        Assert.assertTrue(exec.declaredVariables.size() == 2);
    }

    /**
     * There's proposal to add an entity assignment statement,
     * But until then, using an entity in an OperatorExpr would automatically create it
     * Regardless of whether it's Dependency operator or custom ones
     */
    @Test
    public void testAutoCreateEntityOnFill() throws Exception {
        // get accessible exec environment
        ExecEnvAccess exec = new ExecEnvAccess(repl);
        repl.execute("Entity e1: 'Observer Interface'");
        repl.execute("Entity e2: 'Concrete Observer'");
        repl.execute("Bucket b: 'Simple Observer Pattern'");

        Assert.assertFalse(exec.bucketList.keySet().isEmpty());
        Assert.assertFalse(exec.declaredVariables.isEmpty());
        Assert.assertTrue(exec.bucketList.get("b").isEmpty()); // this bucket should be empty

        // todo: should fail for filter

        repl.execute("b <= e2 IMPLEMENT e1");
        Assert.assertTrue(exec.bucketList.get("b").size() == 2);
    }

    /**
     *
     */
    @Test
    public void testFillStatement_Dependency_IMPLEEMENT() throws Exception {
        ExecEnvAccess exec = new ExecEnvAccess(repl);
        repl.execute("Entity e1: 'Observer Interface'");
        repl.execute("Entity e2: 'Concrete Observer'");
        repl.execute("Bucket b: 'Simple Observer Pattern'");

        repl.execute("Print b");
        repl.execute("b <= e2 IMPLEMENT e1");
        repl.execute("Print b");
        Entity e1 = exec.bucketList.get("b").get("e1");
        Entity e2 = exec.bucketList.get("b").get("e2");

        Assert.assertTrue(e1.size() == 1);
        Assert.assertTrue(e1.hasClass(1));
        Assert.assertTrue(e2.size() == 2);
        Assert.assertTrue(e2.hasClass(0) && e2.hasClass(4));

        repl.execute("b => e2 IMPLEMENT e1");
        repl.execute("Print b");
        Assert.assertTrue(e1.isEmpty());
        Assert.assertTrue(e2.isEmpty());

        repl.execute("b <= e2 IMPLEMENT e1");
        Assert.assertTrue(e1.size() == 1);
        Assert.assertTrue(e1.hasClass(1));
        Assert.assertTrue(e2.size() == 2);
        Assert.assertTrue(e2.hasClass(0) && e2.hasClass(4));
        repl.execute("Print b");
    }

    @Test
    public void testFillStatement_Dependency_Specialize() throws Exception {
        ExecEnvAccess exec = new ExecEnvAccess(repl);
        repl.execute("Entity e1: 'Observer Interface'");
        repl.execute("Entity e2: 'Concrete Observer'");
        repl.execute("Bucket b: 'Simple Observer Pattern'");

        repl.execute("Print b");
        repl.execute("b <= e2 SPECIALIZE e1");
        repl.execute("Print b");
        Entity e1 = exec.bucketList.get("b").get("e1");
        Entity e2 = exec.bucketList.get("b").get("e2");

        Assert.assertTrue(e1.size() == 1);
        Assert.assertTrue(e1.hasClass(1));
        Assert.assertTrue(e2.size() == 2);
        Assert.assertTrue(e2.hasClass(0) && e2.hasClass(4));

        repl.execute("b => e2 SPECIALIZE e1");
        repl.execute("Print b");
        Assert.assertTrue(e1.isEmpty());
        Assert.assertTrue(e2.isEmpty());

        repl.execute("b <= e2 SPECIALIZE e1");
        Assert.assertTrue(e1.size() == 1);
        Assert.assertTrue(e1.hasClass(1));
        Assert.assertTrue(e2.size() == 2);
        Assert.assertTrue(e2.hasClass(0) && e2.hasClass(4));
        repl.execute("Print b");
    }

    @Test
    public void testFillStatement_MultiDependency() throws Exception {
        ExecEnvAccess exec = new ExecEnvAccess(repl);
        repl.execute("Entity e1: 'Observer Interface'");
        repl.execute("Entity e3: 'Subject'");
        repl.execute("Bucket b: 'Simple Observer Pattern'");

        repl.execute("Print b");
        repl.execute("b <= e3 [typed,use] e1");

        Entity e1 = exec.bucketList.get("b").get("e1");
        Entity e3 = exec.bucketList.get("b").get("e3");

        Assert.assertTrue(e1.size() == 1);
        Assert.assertTrue(e1.hasClass(1));
        Assert.assertTrue(e3.size() == 2);
        Assert.assertTrue(e3.hasClass(2) && e3.hasClass(3));
        repl.execute("Print b");

        repl.execute("b => e3 [typed,use] e1");
        Assert.assertTrue(e1.isEmpty());
        Assert.assertTrue(e3.isEmpty());
        repl.execute("Print b");
    }

    @Test
    public void operatorFunctionTest() throws Exception {
        ExecEnvAccess exec = new ExecEnvAccess(repl);
        repl.execute("Entity e1: 'Observer Interface'");
        repl.execute("Entity e2: 'Concrete Observer'");
        repl.execute("Bucket b: 'Simple Observer Pattern'");

        repl.execute("Print b");
        repl.execute("b <= e2 SPECIALIZE e1");
        repl.execute("b <= e1 SPECIALIZE e2");
        Entity e1 = exec.bucketList.get("b").get("e1");
        Entity e2 = exec.bucketList.get("b").get("e2");
        repl.execute("Print b");

        Assert.assertFalse(e1.isEmpty());
        Assert.assertTrue(e1.size() == e2.size());

        int currSize = e1.size();

        //repl.execute("b <= 'notify' invalid_operator e1"); //should throw exception
        repl.execute("b => e2 xors e1");
        Assert.assertFalse(e1.size() == e2.size());
        Assert.assertTrue(e2.isEmpty());
        Assert.assertTrue(e1.size() == currSize);
        repl.execute("Print b");
    }
}
