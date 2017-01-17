package DPD.REPL;

import DPD.Browser.EasyDSMQuery;
import DPD.Model.*;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Justice on 1/10/2017.
 */
public class ExecEnv {

    private HashMap<String, String> declaredVariables;
    private HashMap<String, Bucket> bucketList;
    private EasyDSMQuery dsmQuery;
    private OperatorFunctions opFunc;

    public ExecEnv(EasyDSMQuery dsmBrowser) {
        declaredVariables = new HashMap<>();
        bucketList = new HashMap<>();
        this.dsmQuery = dsmBrowser;
        opFunc = new OperatorFunctions();
        opFunc.initDefaults();
    }

    public void createEntity(String entityId, String name) throws Exception {
        assertUndeclared(entityId);
        declaredVariables.put(entityId, name);
    }


    public void createBucket(String bucketId, String name) throws Exception {
        assertUndeclared(bucketId);

        declaredVariables.put(bucketId, name);
        bucketList.put(bucketId, new Bucket());
    }

    public void fillBucket(String bucketId, DependencyType dependency, String leftOperand, String rightOperand) throws Exception {
        List<DependencyType> d = new ArrayList<>();
        d.add(dependency);
        fillBucket(bucketId, d, leftOperand, rightOperand);
    }



    public void fillBucket(String bucketId, List<DependencyType> dependency, String leftOperand, String rightOperand) throws Exception {
        assertDeclared(bucketId);
        Tuple t = new Tuple();
        dsmQuery.populate(dependency, t);

        Bucket b = bucketList.get(bucketId);
        b.addIfNotExists(leftOperand, rightOperand);
        setGroupId(t, b.get(rightOperand));
        b.get(leftOperand).addAll(t.X);
        b.get(rightOperand).addAll(t.Y);
    }

    public void fillBucket(String bucketId, String operator, String leftOperand, String rightOperand) throws Exception {
        assertDeclared(bucketId);
        OperatorObject op = opFunc.get(operator);
        if( op == null )
            throw new NotImplementedException();

        Tuple t = new Tuple();
        Bucket b = bucketList.get(bucketId);
        op.func.call(b, leftOperand, rightOperand, t);

        if(isDeclared(leftOperand))
            b.addIfNotExists(leftOperand);
        b.addIfNotExists(rightOperand);

        setGroupId(t, b.get(rightOperand));

        if(isDefined(b, leftOperand))
            b.get(leftOperand).addAll(t.X);
        b.get(rightOperand).addAll(t.Y);
    }

    public void filterBucket(String bucketId, List<DependencyType> dependencies, String leftOperand, String rightOperand) throws Exception {
        assertDeclared(bucketId, leftOperand, rightOperand);

        Tuple t = new Tuple();
        dsmQuery.populate(dependencies, t);

        Bucket b = bucketList.get(bucketId);
        setGroupId(t, b.get(rightOperand));

        b.get(leftOperand).removeByClassId(t.X);
        b.get(rightOperand).removeByClassId(t.Y);
    }

    public void filterBucket(String bucketId, DependencyType dependency, String leftOperand, String rightOperand) throws Exception {
        List<DependencyType> dt = new ArrayList<>();
        dt.add(dependency);
        filterBucket(bucketId, dt, leftOperand, rightOperand);
    }

    public void filterBucket(String bucketId, String operator, String leftOperand, String rightOperand) throws Exception {
        assertDeclared(bucketId, leftOperand, rightOperand);
        OperatorObject op = opFunc.get(operator);
        if( op == null )
            throw new NotImplementedException();

        Tuple t = new Tuple();
        Bucket b = bucketList.get(bucketId);
        op.func.call(b, leftOperand, rightOperand, t);

        setGroupId(t, b.get(rightOperand));

        if(isDefined(b, leftOperand))
            b.get(leftOperand).removeByClassId(t.X);
        b.get(rightOperand).removeByClassId(t.Y);
    }

    public void promoteBucket(String bucketId, DependencyType dependency, String leftOperand, String rightOperand) throws Exception {
        assertDeclared(bucketId, leftOperand, rightOperand);

        Tuple t = new Tuple();
        dsmQuery.populate(dependency, t);

        Bucket b = bucketList.get(bucketId);
        setGroupId(t, b.get(rightOperand));

        b.get(leftOperand).promoteAll(t.X);
        b.get(rightOperand).promoteAll(t.Y);
    }

    public void demoteBucket(String bucketId, DependencyType dependency, String leftOperand, String rightOperand) throws Exception {
        assertDeclared(bucketId, leftOperand, rightOperand);

        Tuple t = new Tuple();
        dsmQuery.populate(dependency, t);

        Bucket b = bucketList.get(bucketId);
        setGroupId(t, b.get(rightOperand));

        b.get(leftOperand).demoteAll(t.X);
        b.get(rightOperand).demoteAll(t.Y);

    }

    public void resolveBucket(String bucketId, String variableId) throws Exception {
        assertDeclared(bucketId, variableId);

        Bucket b = bucketList.get(bucketId);
        for(int pocket = 0; pocket < b.getPocket(); pocket++) {
            boolean allHaveIt = true;
            for(String entityId: b.keySet()) {
                allHaveIt = b.get(entityId).hasPocket(pocket);
            }
            if(!allHaveIt) {
                for(String entityId: b.keySet()) {
                    b.get(entityId).removePocket(pocket);
                }
            }
        }
    }

    /**
     * Prints bucket and bucket entities if specified
     * input types bucket or bucket.entity
     * @param objectId
     * @throws Exception
     */
    public void printObject(String objectId) throws Exception {
        if(bucketList.containsKey(objectId))
            System.out.println(objectId + ":\n" +bucketList.get(objectId));
        else if(objectId.contains(".")) {
            String[] vars =  objectId.split(".");
            String entity = vars.length > 1 ? vars[1] : "";
            if( declaredVariables.containsKey(vars[0]) && declaredVariables.containsKey(entity))
                System.out.println(bucketList.get(vars[0]).get(vars[1]));
        }
    }

    private void setGroupId(Tuple t, Entity entity) {
        // t.Y is the pivot by default
        for(CNode cn: t.Y) {
            if(entity.hasClass(cn.classId)) {
                int existingPocket = entity.getByClassId(cn.classId).pocket;
                for(int i = 0; i < t.X.size(); i++){
                    if(t.X.get(i).pocket == cn.pocket){
                        t.X.get(i).pocket = existingPocket;
                    }
                }
            }
            // otherwise, they keep their pockets.
        }
    }

    public void assertDeclared(String... variableIds) throws Exception {
        for(String var: variableIds) {
            if( !declaredVariables.containsKey(var) )
                throw new Exception(var + " is an undeclared variable");
        }
    }


    private void assertUndeclared(String... variableIds) throws Exception {
        for(String var: variableIds) {
            if( declaredVariables.containsKey(var) )
                throw new Exception(var + " is already defined");
        }
    }

    private boolean isDeclared(String... variableIds) {
        for(String var: variableIds) {
            if( !declaredVariables.containsKey(var) )
                return false;
        }
        return true;
    }

    private boolean isDefined(Bucket b, String... variableIds) {
        return b.keySet().containsAll(Arrays.asList(variableIds));
    }
}
