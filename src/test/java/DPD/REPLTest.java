package DPD;

import DPD.Browser.EasyDSMQuery;
import DPD.Model.Bucket;
import DPD.Model.DSMFileModel;
import DPD.Model.DependencyType;
import DPD.Model.Entity;
import DPD.REPL.ExecEnv;
import DPD.REPL.FileREPL;
import DPD.REPL.OperatorFunctions;
import DPD.REPL.REPL;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
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
    public void testFillStatement_Dependency() throws Exception {
        ExecEnvAccess exec = new ExecEnvAccess(repl);
        repl.execute("Entity e1: 'Observer Interface'");
        repl.execute("Entity e2: 'Concrete Observer'");
        repl.execute("Bucket b: 'Simple Observer Pattern'");
        repl.execute("b <= e2 IMPLEMENT e1");

        Entity e1 = exec.bucketList.get("b").get("e1");
        Entity e2 = exec.bucketList.get("b").get("e2");

        Assert.assertTrue(e1.size() == 1);
        Assert.assertTrue(e2.size() == 2);

        repl.execute("b => e2 IMPLEMENT e1");
        Assert.assertTrue(e1.isEmpty());
        Assert.assertTrue(e2.isEmpty());

        repl.execute("b => e2 SPECIALIZE e1");
        Assert.assertTrue(e1.size() == 1);
        Assert.assertTrue(e2.size() == 2);
    }
}
