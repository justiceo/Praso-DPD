package DPD;

import DPD.Browser.EasyDSMQuery;
import DPD.Model.CNode;
import DPD.Model.DSMFileModel;
import DPD.Model.DependencyType;
import DPD.Model.Entity;
import DPD.REPL.Evaluator;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertTrue;

/**
 * Created by Justice on 1/10/2017.
 */
public class REPLTest {

    Evaluator repl;
    EasyDSMQuery dsmBrowser;
    String testDsmFile = "files/test.dsm";

    @Before
    public void setup() {
        DSMFileModel dsm = new DSMFileModel(testDsmFile);
        List<DependencyType> dependencyTypeList = Util.getDependencyTypes(dsm.exhibitedDependencyLine);
        dsmBrowser = new EasyDSMQuery(dsm.matrixLines, dsm.filePaths, dependencyTypeList);
        repl = new Evaluator(dsmBrowser);
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
        assertTrue(exec.bucketList.keySet().isEmpty());
        assertTrue(exec.declaredVariables.isEmpty());

        // after execution, declaredVariables should contain 1 item
        String line = "Entity observerI: 'Observer Interface'";
        repl.execute(line);
        assertTrue(exec.bucketList.keySet().isEmpty());
        assertTrue(exec.declaredVariables.size() == 1);

        // let's add a bucket to the mix
        line = "Bucket b: 'Simple Observer Pattern'";
        repl.execute(line);
        assertTrue(exec.bucketList.keySet().size() == 1);
        assertTrue(exec.declaredVariables.size() == 2);
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
        assertTrue(exec.bucketList.get("b").isEmpty()); // this bucket should be empty

        // todo: should fail for filter

        repl.execute("b <= e2 IMPLEMENT e1");
        assertTrue(exec.bucketList.get("b").size() == 2);
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

        assertTrue(e1.size() == 1);
        assertTrue(e1.hasClass(1));
        assertTrue(e2.size() == 2);
        assertTrue(e2.hasClass(0) && e2.hasClass(4));

        repl.execute("b => e2 IMPLEMENT e1");
        repl.execute("Print b");
        assertTrue(e1.isEmpty());
        assertTrue(e2.isEmpty());

        repl.execute("b <= e2 IMPLEMENT e1");
        assertTrue(e1.size() == 1);
        assertTrue(e1.hasClass(1));
        assertTrue(e2.size() == 2);
        assertTrue(e2.hasClass(0) && e2.hasClass(4));
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

        assertTrue(e1.size() == 1);
        assertTrue(e1.hasClass(1));
        assertTrue(e2.size() == 2);
        assertTrue(e2.hasClass(0) && e2.hasClass(4));

        repl.execute("b => e2 SPECIALIZE e1");
        repl.execute("Print b");
        assertTrue(e1.isEmpty());
        assertTrue(e2.isEmpty());

        repl.execute("b <= e2 SPECIALIZE e1");
        assertTrue(e1.size() == 1);
        assertTrue(e1.hasClass(1));
        assertTrue(e2.size() == 2);
        assertTrue(e2.hasClass(0) && e2.hasClass(4));
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

        assertTrue(e1.size() == 1);
        assertTrue(e1.hasClass(1));
        assertTrue(e3.size() == 2);
        assertTrue(e3.hasClass(2) && e3.hasClass(3));
        repl.execute("Print b");

        repl.execute("b => e3 [typed,use] e1");
        assertTrue(e1.isEmpty());
        assertTrue(e3.isEmpty());
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
        assertTrue(e1.size() == e2.size());

        int currSize = e1.size();

        //repl.execute("b <= 'notify' invalid_operator e1"); //should throw exception
        repl.execute("b => e2 and e1");
        Assert.assertFalse(e1.size() == e2.size());
        assertTrue(e2.isEmpty());
        assertTrue(e1.size() == currSize);
        repl.execute("Print b");
    }


    @Test
    public void operatorFunctionPocketSizeTest() throws Exception {
        ExecEnvAccess exec = new ExecEnvAccess(repl);
        repl.execute("Entity e1: 'Observer Interface'");
        repl.execute("Entity e2: 'Concrete Observer'");
        repl.execute("Bucket b: 'Simple Observer Pattern'");

        repl.execute("Print b");
        repl.execute("b <= e2 SPECIALIZE e1");
        Entity e1 = exec.bucketList.get("b").get("e1");
        Entity e2 = exec.bucketList.get("b").get("e2");

        Assert.assertFalse(e1.isEmpty());
        Assert.assertFalse(e2.isEmpty());

        int currSize = e1.size();

        repl.execute("b => 1 pocket_size e2");
        assertTrue(e1.size() == 1);
        assertTrue(e2.size() == 2);

        repl.execute("b => 1 pocket_size e1");
        assertTrue(e1.isEmpty());
        assertTrue(e2.size() == 2);
        repl.execute("Print b");
    }

    @Test
    public void testResolveBucket() throws Exception {
        ExecEnvAccess exec = new ExecEnvAccess(repl);
        repl.execute("Entity e1: 'Observer Interface'");
        repl.execute("Entity e2: 'Concrete Observer'");
        repl.execute("Entity e3: 'Subject'");
        repl.execute("Bucket b: 'Simple Observer Pattern'");

        repl.execute("Print b");
        repl.execute("b <= e2 SPECIALIZE e1");
        repl.execute("Print b");
        repl.execute("b <= e3 TYPED e1 ");
        repl.execute("Print b");

        Entity e1 = exec.bucketList.get("b").get("e1");
        Entity e2 = exec.bucketList.get("b").get("e2");
        Entity e3 = exec.bucketList.get("b").get("e3");

        assertTrue(!e1.isEmpty() && !e2.isEmpty() && !e3.isEmpty());

        int e1size = e1.size();
        int e2size = e2.size();
        int e3size = e3.size();

        assertTrue(e1size == 1 && e2size == 2 && e3size == 2);

        repl.execute("Resolve b");
        repl.execute("Print b");
        assertTrue(e1size == e1.size() && e2size == e2.size() && e3size == e3.size());

        // clear any of them and the rest should be cleared
        e3.clear();
        repl.execute("Resolve b");
        repl.execute("Print b");
        assertTrue(e1.isEmpty() && e2.isEmpty() && e3.isEmpty());
    }


    @Test
    public void testMethodName() throws Exception {
        ExecEnvAccess exec = new ExecEnvAccess(repl);
        repl.execute("Entity e1: 'Observer Interface'");
        repl.execute("Entity e2: 'Concrete Observer'");
        repl.execute("Entity e3: 'Subject'");
        repl.execute("Bucket b: 'Simple Observer Pattern'");

        repl.execute("Print b");
        repl.execute("b <= e2 SPECIALIZE e1");
        repl.execute("Print b");
        repl.execute("b => 'notify' not_method_name e2");

        Entity e1 = exec.bucketList.get("b").get("e1");
        Entity e2 = exec.bucketList.get("b").get("e2");

        assertTrue(e2.size() == 2 && e1.size() == 1);

        repl.execute("b => 'notify' method_name e2");
        assertTrue(e2.size() == 0 && e1.size() == 1);

        repl.execute("Print b");
    }


    @Test
    public void testPromoteAndDemote() throws Exception {
        ExecEnvAccess exec = new ExecEnvAccess(repl);
        repl.execute("Entity e1: 'Observer Interface'");
        repl.execute("Entity e2: 'Concrete Observer'");
        repl.execute("Entity e3: 'Subject'");
        repl.execute("Bucket b: 'Simple Observer Pattern'");

        repl.execute("Print b");
        repl.execute("b <= e2 SPECIALIZE e1");
        repl.execute("Print b");
        repl.execute("b ++ 2 pocket_size e2");

        Entity e1 = exec.bucketList.get("b").get("e1");
        Entity e2 = exec.bucketList.get("b").get("e2");

        assertTrue(e2.size() == 2 && e1.size() == 1);

        for(CNode c: e2) {
            assertTrue(c.score == 1);
        }

        repl.execute("b -- 2 pocket_size e2");
        for(CNode c: e2) {
            assertTrue(c.score == 0);
        }
        repl.execute("Print b");
    }
}
