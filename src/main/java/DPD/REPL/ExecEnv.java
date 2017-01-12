package DPD.REPL;

import DPD.DSMMapper.Bucket;
import DPD.DSMMapper.DepNode;
import DPD.DSMQuery;
import DPD.EasyDSMQuery;
import DPD.Enums.DependencyType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Justice on 1/10/2017.
 */
public class ExecEnv {

    private HashMap<String, String> declaredVariables;
    private HashMap<String, Bucket> bucketList;
    private HashMap<String, String> fillFunctions;
    private DSMQuery dsmBrowser;
    EasyDSMQuery easyDSMQuery = null;

    public ExecEnv(DSMQuery dsmBrowser) {
        declaredVariables = new HashMap<>();
        bucketList = new HashMap<>();
        this.dsmBrowser = dsmBrowser;
    }

    public void createEntity(String entityId, String name) throws Exception {
        System.out.println(entityId + " " + name);
        assertUndefined(entityId);
        declaredVariables.put(entityId, name);
    }


    public void createBucket(String bucketId, String name) throws Exception {
        System.out.println(bucketId + " " + name);
        assertUndefined(bucketId);

        declaredVariables.put(bucketId, name);
        bucketList.put(bucketId, new Bucket());
    }

    public void fillBucket(String bucketId, DependencyType dependency, String leftOperand, String rightOperand) throws Exception {
        System.out.println(bucketId + " " + dependency);
        assertDefined(bucketId, leftOperand, rightOperand);

        Tuple t = new Tuple();
        easyDSMQuery.populate(dependency, t.X, t.Y);
        setGroupId(t);

        Bucket b = bucketList.get(bucketId);
        b.get(leftOperand).addAll(t.X);
        b.get(rightOperand).addAll(t.Y);
    }

    public void filterBucket(String bucketId, DependencyType dependency, String leftOperand, String rightOperand) throws Exception {
        System.out.println(bucketId + " " + dependency);
        assertDefined(bucketId, leftOperand, rightOperand);

        Tuple t = new Tuple();
        easyDSMQuery.populate(dependency, t.X, t.Y);
        setGroupId(t);

        Bucket b = bucketList.get(bucketId);
        b.get(leftOperand).removeAll(t.X);
        b.get(rightOperand).removeAll(t.Y);
    }

    private void setGroupId(Tuple t) {

    }

    public void promoteBucket(String bucketId, DependencyType dependency, String leftOperand, String rightOperand) throws Exception {
        System.out.println(bucketId + " " + dependency);
        System.out.println(bucketId + " " + dependency);
        assertDefined(bucketId, leftOperand, rightOperand);

        Tuple t = new Tuple();
        easyDSMQuery.populate(dependency, t.X, t.Y);
        setGroupId(t);

        Bucket b = bucketList.get(bucketId);
        b.get(leftOperand).promoteAll(t.X);
        b.get(rightOperand).promoteAll(t.Y);
    }

    public void demoteBucket(String bucketId, DependencyType dependency, String leftOperand, String rightOperand) throws Exception {
        System.out.println(bucketId + " " + dependency);
        assertDefined(bucketId, leftOperand, rightOperand);

        Tuple t = new Tuple();
        easyDSMQuery.populate(dependency, t.X, t.Y);
        setGroupId(t);

        Bucket b = bucketList.get(bucketId);
        b.get(leftOperand).demoteAll(t.X);
        b.get(rightOperand).demoteAll(t.Y);

    }

    public void resolveBucket(String bucketId, String variableId) throws Exception {
        System.out.println(bucketId);
        assertDefined(bucketId, variableId);


    }

    /**
     * Prints bucket and bucket entities if specified
     * input types bucket or bucket.entity
     * @param objectId
     * @throws Exception
     */
    public void printObject(String objectId) throws Exception {
        assertDefined(objectId);
        if(bucketList.containsKey(objectId))
            System.out.println(bucketList.get(objectId));
        else if(objectId.contains(".")) {
            String[] vars =  objectId.split(".");
            String entity = vars.length > 1 ? vars[1] : "";
            if( declaredVariables.containsKey(vars[0]) && declaredVariables.containsKey(entity))
                System.out.println(bucketList.get(vars[0]).get(vars[1]));
        }
    }

    public void assertDefined(String... variableIds) throws Exception {
        for(String var: variableIds) {
            if( !declaredVariables.containsKey(var) )
                throw new Exception(var + "is an undeclared variable");
        }
    }


    private void assertUndefined(String... variableIds) throws Exception {
        for(String var: variableIds) {
            if( declaredVariables.containsKey(var) )
                throw new Exception(var + "is already defined");
        }
    }

    class Tuple {
        List<DepNode> X = new ArrayList<>();
        List<DepNode> Y = new ArrayList<>();
    }
}
