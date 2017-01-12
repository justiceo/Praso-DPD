package DPD.REPL;

import DPD.DSMMapper.Bucket;
import DPD.DSMMapper.CNode;
import DPD.DSMMapper.Entity;
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
        assertUndeclared(entityId);
        declaredVariables.put(entityId, name);
    }


    public void createBucket(String bucketId, String name) throws Exception {
        System.out.println(bucketId + " " + name);
        assertUndeclared(bucketId);

        declaredVariables.put(bucketId, name);
        bucketList.put(bucketId, new Bucket());
    }

    public void fillBucket(String bucketId, DependencyType dependency, String leftOperand, String rightOperand) throws Exception {
        System.out.println(bucketId + " " + dependency);
        assertDeclared(bucketId);


        Tuple t = new Tuple();
        easyDSMQuery.populate(dependency, t.X, t.Y);

        Bucket b = bucketList.get(bucketId);
        setGroupId(t, b.get(rightOperand));

        b.addIfNotExists(leftOperand, rightOperand);
        b.get(leftOperand).addAll(t.X);
        b.get(rightOperand).addAll(t.Y);
    }

    public void filterBucket(String bucketId, DependencyType dependency, String leftOperand, String rightOperand) throws Exception {
        System.out.println(bucketId + " " + dependency);
        assertDeclared(bucketId, leftOperand, rightOperand);

        Tuple t = new Tuple();
        easyDSMQuery.populate(dependency, t.X, t.Y);

        Bucket b = bucketList.get(bucketId);
        setGroupId(t, b.get(rightOperand));

        b.get(leftOperand).removeAll(t.X);
        b.get(rightOperand).removeAll(t.Y);
    }

    public void promoteBucket(String bucketId, DependencyType dependency, String leftOperand, String rightOperand) throws Exception {
        System.out.println(bucketId + " " + dependency);
        System.out.println(bucketId + " " + dependency);
        assertDeclared(bucketId, leftOperand, rightOperand);

        Tuple t = new Tuple();
        easyDSMQuery.populate(dependency, t.X, t.Y);

        Bucket b = bucketList.get(bucketId);
        setGroupId(t, b.get(rightOperand));

        b.get(leftOperand).promoteAll(t.X);
        b.get(rightOperand).promoteAll(t.Y);
    }

    public void demoteBucket(String bucketId, DependencyType dependency, String leftOperand, String rightOperand) throws Exception {
        System.out.println(bucketId + " " + dependency);
        assertDeclared(bucketId, leftOperand, rightOperand);

        Tuple t = new Tuple();
        easyDSMQuery.populate(dependency, t.X, t.Y);

        Bucket b = bucketList.get(bucketId);
        setGroupId(t, b.get(rightOperand));

        b.get(leftOperand).demoteAll(t.X);
        b.get(rightOperand).demoteAll(t.Y);

    }

    public void resolveBucket(String bucketId, String variableId) throws Exception {
        System.out.println(bucketId);
        assertDeclared(bucketId, variableId);


    }

    /**
     * Prints bucket and bucket entities if specified
     * input types bucket or bucket.entity
     * @param objectId
     * @throws Exception
     */
    public void printObject(String objectId) throws Exception {
        assertDeclared(objectId);
        if(bucketList.containsKey(objectId))
            System.out.println(bucketList.get(objectId));
        else if(objectId.contains(".")) {
            String[] vars =  objectId.split(".");
            String entity = vars.length > 1 ? vars[1] : "";
            if( declaredVariables.containsKey(vars[0]) && declaredVariables.containsKey(entity))
                System.out.println(bucketList.get(vars[0]).get(vars[1]));
        }
    }

    private void setGroupId(Tuple t, Entity entity) {
        // t.Y is the pivot by default
        for(CNode dn: t.Y) {
            if(entity.hasClass(dn.classId)) {
                int pocket = entity.getByClass(dn.classId);
            }
        }
    }

    public void assertDeclared(String... variableIds) throws Exception {
        for(String var: variableIds) {
            if( !declaredVariables.containsKey(var) )
                throw new Exception(var + "is an undeclared variable");
        }
    }


    private void assertUndeclared(String... variableIds) throws Exception {
        for(String var: variableIds) {
            if( declaredVariables.containsKey(var) )
                throw new Exception(var + "is already defined");
        }
    }

    class Tuple {
        List<CNode> X = new ArrayList<>();
        List<CNode> Y = new ArrayList<>();
    }
}
