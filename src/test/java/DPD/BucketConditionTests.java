package DPD;

import DPD.Browser.EasyDSMQuery;
import DPD.Model.*;
import DPD.REPL.Environment;
import DPD.REPL.Evaluator;
import DPD.REPL.Evaluator.StatementType;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertSame;
import static org.junit.Assert.assertTrue;

/**
 * Created by I853985 on 2/1/2017.
 */
public class BucketConditionTests {

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
    public void implementTest() throws Exception {
        Environment env = getReadyObserverEnv();
        BucketResult implResult = env.evalDependency(DependencyType.IMPLEMENT, "e2", "e1");
        List<String> expectedKeys = Util.extractList("[e1, e2]");
        assertTrue(implResult.keySet().size() == expectedKeys.size() );
        assertTrue(implResult.keySet().containsAll(expectedKeys));

        Entity e1 = implResult.get("e1");
        Entity e2 = implResult.get("e2");

        assertTrue(e1.size() == 1);
        assertTrue(e1.hasClass(1));
        assertTrue(e2.size() == 2);
        assertTrue(e2.hasClass(0) && e2.hasClass(4));
    }

    @Test
    public void FillTest() throws Exception {
        Environment env = getReadyObserverEnv();
        BucketResult implResult = env.evalDependency(DependencyType.IMPLEMENT, "e2", "e1");
        Bucket newBucket = new Bucket();
        Bucket b = env.evalBucketStatement(newBucket, StatementType.FillStatement, implResult);
        assertSame(b, newBucket);
        assertTrue(b.size() == 2);
        assertTrue(b.keySet().containsAll(Util.list("e2", "e1")));

        Entity e1 = b.get("e1");
        Entity e2 = b.get("e2");
        CNode observerInterface = new CNode(1, 1);

        assertTrue(e1.size() == 1);
        assertTrue(e1.hasClass(1));
        assertTrue(e1.containsValue(observerInterface));
        assertTrue(e2.size() == 2);
        assertTrue(e2.hasClass(0) && e2.hasClass(4));

        env.evalBucketStatement(b, StatementType.FillStatement, implResult);

        assertTrue(e1.size() == 1);
        assertTrue(e1.hasClass(1));
        assertTrue(e1.containsValue(observerInterface));
        assertTrue(e2.size() == 2);
        assertTrue(e2.hasClass(0) && e2.hasClass(4));

        implResult = env.evalDependency(DependencyType.TYPED, "e2", "e1");
        env.evalBucketStatement(b, StatementType.FillStatement, implResult);
        assertTrue(e1.size() == 1);
        observerInterface.pocket = 2;
        assertTrue(e1.containsValue(observerInterface));
        assertTrue(e2.size() == 4);
        assertTrue(e2.keySet().containsAll(Util.list(0, 2, 3, 4)));
    }

    @Test
    public void AssignTest() throws Exception {
        Environment env = getReadyObserverEnv();
        BucketResult implResult = env.evalDependency(DependencyType.IMPLEMENT, "e2", "e1");
        Bucket newBucket = new Bucket();
        Bucket b = env.evalBucketStatement(newBucket, StatementType.FillStatement, implResult);
        env.evalBucketStatement(b, StatementType.OverwriteStatement, implResult);
        Entity e1 = b.get("e1");
        Entity e2 = b.get("e2");
        CNode observerInterface = new CNode(1, 1);
        assertTrue(e1.size() == 1);
        assertTrue(e1.hasClass(1));
        assertTrue(e1.containsValue(observerInterface));
        assertTrue(e2.size() == 2);
        assertTrue(e2.keySet().containsAll(Util.list(0, 4)));

        implResult = env.evalDependency(DependencyType.TYPED, "e2", "e1");
        env.evalBucketStatement(b, StatementType.OverwriteStatement, implResult);
        Entity e1again = b.get("e1");
        assertSame(e1, e1again);
        assertTrue(e1.size() == 0);
        assertTrue(e2.size() == 0);


        b.clear();
        implResult = env.evalDependency(DependencyType.TYPED, "e2", "e1");
        env.evalBucketStatement(b, StatementType.FillStatement, implResult);
        System.out.println(b);
        env.evalBucketStatement(b, StatementType.OverwriteStatement, implResult);
        e1again = b.get("e1");
        //e1 = e1again;
        assertSame(e1, e1again);
        System.out.println(e1.size() + ", " + b);
        assertTrue(e1.size() == 1);
        assertTrue(e1.containsValue(observerInterface));
        assertTrue(e2.size() == 2);
        assertTrue(e2.keySet().containsAll(Util.list(2, 3)));
    }

    @Test
    public void methodNameTest() throws Exception {
        Environment env = getReadyObserverEnv();
        BucketResult b = env.evalDependency(DependencyType.IMPLEMENT, "e2", "e1");
        BucketResult actualResult = env.evalFunction(b, "method_name", "notify", "e1");
        assertTrue(actualResult.keySet().size() == 1);
        Entity e1 = actualResult.get("e1");
        assertTrue(e1.size() == 1 && e1.hasClass(1));
        actualResult = env.evalFunction(b, "method_name", "notify", "e2");
        assertTrue(actualResult.keySet().size() == 1);
        Entity e2 = actualResult.get("e2");
        assertTrue(e2.size() == 2);
        assertTrue(e2.hasClass(0) && e2.hasClass(4));
    }



    @Test
    public void typeNameTest() throws Exception {
        Environment env = getReadyObserverEnv();
        BucketResult b = env.evalDependency(DependencyType.IMPLEMENT, "e2", "e1");
        BucketResult actualResult = env.evalFunction(b, "type_name", "observer", "e1");
        assertTrue(actualResult.keySet().size() == 1);
        Entity e1 = actualResult.get("e1");
        assertTrue(e1.size() == 1 && e1.hasClass(1));
        actualResult = env.evalFunction(b, "type_name", "IObserver", "e2");
        assertTrue(actualResult.keySet().size() == 0);

        actualResult = env.evalFunction(b, "type_name", "observer", "e2");
        assertTrue(actualResult.keySet().size() == 1);
        Entity e2 = actualResult.get("e2");
        assertTrue(e2.size() == 2);
        assertTrue(e2.hasClass(0) && e2.hasClass(4));
    }

    /***
     * Evaluating a pocket_size on simple observer should return the e2 bucket with both elements in it
     */
    @Test
    public void pocket_size_test() throws Exception {
        Environment env = getReadyObserverEnv();
        BucketResult implResult = env.evalDependency(DependencyType.IMPLEMENT, "e2", "e1");
        BucketResult result = env.evalFunction(implResult, "pocket_size", "2", "e2");

        List<String> expectedKeys = Util.extractList("[e2]");
        assertTrue(result.keySet().size() == expectedKeys.size() );
        assertTrue(result.keySet().containsAll(expectedKeys));

        Entity e2 = result.get("e2");
        assertTrue(e2.size() == 2);

        // empty entity
        result = env.evalFunction(implResult, "pocket_size", "2", "e1");
        assertTrue(result.keySet().isEmpty() && result.isEmpty());

        // make e2 singular
        implResult.get("e2").get(0).pocket = 1000;
        result = env.evalFunction(implResult, "pocket_size", "2", "e2");
        assertTrue(result.keySet().isEmpty() && result.isEmpty());

        // test original scenario once again
        implResult = env.evalDependency(DependencyType.IMPLEMENT, "e2", "e1");
        result = env.evalFunction(implResult, "pocket_size", "2", "e2");
        expectedKeys = Util.extractList("[e2]");
        assertTrue(result.keySet().size() == expectedKeys.size() );
        assertTrue(result.keySet().containsAll(expectedKeys));

        e2 = result.get("e2");
        assertTrue(e2.size() == 2);
        assertTrue(e2.hasClass(0) && e2.hasClass(4));
    }

    @Test
    public void trimToMatchTest() throws Exception {
        Environment env = getReadyObserverEnv();
        Bucket empty = new Bucket();
        BucketResult implResult = env.evalDependency(DependencyType.IMPLEMENT, "e2", "e1");
        assertTrue(implResult.get("e2").size() == 2 && implResult.get("e1").size() == 1);
        empty = Environment.trimToMatchBucket(empty, implResult);
        assertTrue(empty.keySet().isEmpty());
        empty = Environment.trimToMatchBucket(implResult, implResult);
        assertFalse(empty.keySet().isEmpty());
        assertTrue(empty.get("e2").size() == 2 && empty.get("e1").size() == 1);
    }

    private Environment getReadyObserverEnv() throws Exception {
        Environment env = ExecEnvAccess.getExecEnv(repl);
        env.createEntity("e1", "Observer Interface");
        env.createEntity("e2", "Concrete observer");
        env.createEntity("e3", "Subject");
        env.createBucket("b1", "Observer bucket");
        return env;
    }

    @Test
    public void getReadyObserverEnvTest() throws Exception {
        Environment env = ExecEnvAccess.getExecEnv(repl);
        env.createEntity("e1", "Observer Interface");
        env.createEntity("e2", "Concrete observer");
        env.createEntity("e3", "Subject");
        env.createBucket("b1", "Observer bucket");

        ExecEnvAccess envAccess = new ExecEnvAccess(env);
        List<String> expectedKeys = Util.extractList("[e1, e2, e3, b1]");
        assertTrue(envAccess.declaredVariables.size() == expectedKeys.size());
        assertTrue(envAccess.declaredVariables.keySet().containsAll(expectedKeys));
        assertTrue(envAccess.bucketList.keySet().size() == 1);
        assertTrue(envAccess.bucketList.keySet().contains("b1"));
    }
}
