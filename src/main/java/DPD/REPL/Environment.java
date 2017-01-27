package DPD.REPL;

import DPD.Browser.EasyDSMQuery;
import DPD.Model.*;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.*;

/**
 * Created by Justice on 1/10/2017.
 */
public class Environment {

    private HashMap<String, String> declaredVariables;
    private HashMap<String, Bucket> bucketList;
    private EasyDSMQuery dsmQuery;
    private OperatorFunctions opFunc;

    public Environment(EasyDSMQuery dsmBrowser) {
        declaredVariables = new HashMap<>();
        bucketList = new HashMap<>();
        this.dsmQuery = dsmBrowser;
        opFunc = new OperatorFunctions(dsmBrowser);
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
        EntityResult t = new EntityResult();
        dsmQuery.populate(dependency, t);

        Bucket b = bucketList.get(bucketId);
        b.addIfNotExists(leftOperand, rightOperand);
        setGroupId(t, b.get(rightOperand));
        b.get(leftOperand).addAll(t.aux);
        b.get(rightOperand).addAll(t.pivot);
    }

    public void fillBucket(String bucketId, String operator, String leftOperand, String rightOperand) throws Exception {
        assertDeclared(bucketId);
        OperatorObject op = opFunc.get(operator);
        if( op == null )
            throw new NotImplementedException();

        EntityResult t = new EntityResult();
        Bucket b = bucketList.get(bucketId);
        op.func.call(b, leftOperand, rightOperand, t);

        /// below lines would need refactoring as they may never be executed

        if( !op.isSingleOperator )
            b.addIfNotExists(leftOperand);
        b.addIfNotExists(rightOperand);

        setGroupId(t, b.get(rightOperand));

        if(isDefined(b, leftOperand))
            b.get(leftOperand).addAll(t.aux);
        b.get(rightOperand).addAll(t.pivot);
    }


    public void overwriteBucket(String bucketId, DependencyType dependency, String leftOperand, String rightOperand) throws Exception {
        List<DependencyType> d = new ArrayList<>();
        d.add(dependency);
        overwriteBucket(bucketId, d, leftOperand, rightOperand);
    }



    public void overwriteBucket(String bucketId, List<DependencyType> dependency, String leftOperand, String rightOperand) throws Exception {
        assertDeclared(bucketId);
        EntityResult t = new EntityResult();
        dsmQuery.populate(dependency, t);

        Bucket b = bucketList.get(bucketId);
        b.addIfNotExists(leftOperand, rightOperand);

        Entity rightE = b.getEntity(rightOperand);
        Entity leftE = b.getEntity(leftOperand);

        setGroupId(t, rightE);

        leftE.resetTo(t.aux);
        rightE.resetTo(t.pivot);
    }

    public void overwriteBucket(String bucketId, String operator, String leftOperand, String rightOperand) throws Exception {
        assertDeclared(bucketId);
        OperatorObject op = opFunc.get(operator);
        if( op == null )
            throw new NotImplementedException();

        EntityResult t = new EntityResult();
        Bucket b = bucketList.get(bucketId);
        op.func.call(b, leftOperand, rightOperand, t);

        /// below lines would need refactoring as they may never be executed

        if( !op.isSingleOperator )
            b.addIfNotExists(leftOperand);
        b.addIfNotExists(rightOperand);

        Entity rightE = b.get(rightOperand);
        Entity leftE = b.get(leftOperand);

        setGroupId(t, rightE);

        if(isDefined(b, leftOperand))
            leftE = t.aux;
        rightE = t.pivot;
    }

    public void filterBucket(String bucketId, List<DependencyType> dependencies, String leftOperand, String rightOperand) throws Exception {
        assertDeclared(bucketId, leftOperand, rightOperand);

        EntityResult t = new EntityResult();
        dsmQuery.populate(dependencies, t);

        Bucket b = bucketList.get(bucketId);
        setGroupId(t, b.get(rightOperand));

        b.get(leftOperand).removeByClassId(t.aux);
        b.get(rightOperand).removeByClassId(t.pivot);
    }

    public void filterBucket(String bucketId, DependencyType dependency, String leftOperand, String rightOperand) throws Exception {
        List<DependencyType> dt = new ArrayList<>();
        dt.add(dependency);
        filterBucket(bucketId, dt, leftOperand, rightOperand);
    }

    public void filterBucket(String bucketId, String operator, String leftOperand, String rightOperand) throws Exception {
        assertDeclared(bucketId, rightOperand);
        OperatorObject op = opFunc.get(operator);
        if( op == null )
            throw new NotImplementedException();

        EntityResult t = new EntityResult();
        Bucket b = bucketList.get(bucketId);
        op.func.call(b, leftOperand, rightOperand, t);

        /// below lines would need refactoring as they may never be executed

        setGroupId(t, b.get(rightOperand));

        if(isDefined(b, leftOperand))
            b.get(leftOperand).removeByClassId(t.aux);
        b.get(rightOperand).removeByClassId(t.pivot);
    }

    public void promoteBucket(String bucketId, DependencyType dependency, String leftOperand, String rightOperand) throws Exception {
        List<DependencyType> dependencyTypes = new ArrayList<>();
        dependencyTypes.add(dependency);
        promoteBucket(bucketId, dependencyTypes, leftOperand, rightOperand);
    }

    public void promoteBucket(String bucketId, List<DependencyType> dependencies, String leftOperand, String rightOperand) throws Exception {
        assertDeclared(bucketId, leftOperand, rightOperand);

        EntityResult t = new EntityResult();

        dsmQuery.populate(dependencies, t);

        Bucket b = bucketList.get(bucketId);
        setGroupId(t, b.get(rightOperand));

        b.get(leftOperand).promoteAll(t.aux);
        b.get(rightOperand).promoteAll(t.pivot);
    }

    public void promoteBucket(String bucketId, String operator, String leftOperand, String rightOperand) throws Exception {
        assertDeclared(bucketId, rightOperand);
        OperatorObject op = opFunc.get(operator);
        if( op == null )
            throw new NotImplementedException();

        EntityResult t = new EntityResult();
        Bucket b = bucketList.get(bucketId);
        op.func.call(b, leftOperand, rightOperand, t);

        /// below lines would need refactoring as they may never be executed

        Entity rightE = b.get(rightOperand);
        Entity leftE = b.get(leftOperand);
        setGroupId(t, b.get(rightOperand));

        if(isDefined(b, leftOperand))
            leftE.promoteAll(t.aux);
        rightE.promoteAll(t.pivot);
    }

    public void demoteBucket(String bucketId, DependencyType dependency, String leftOperand, String rightOperand) throws Exception {
        List<DependencyType> dependencyTypes = new ArrayList<>();
        dependencyTypes.add(dependency);
        demoteBucket(bucketId, dependencyTypes, leftOperand, rightOperand);
    }

    public void demoteBucket(String bucketId, List<DependencyType> dependencies, String leftOperand, String rightOperand) throws Exception {
        assertDeclared(bucketId, leftOperand, rightOperand);

        EntityResult t = new EntityResult();
        dsmQuery.populate(dependencies, t);

        Bucket b = bucketList.get(bucketId);
        setGroupId(t, b.get(rightOperand));

        b.get(leftOperand).demoteAll(t.aux);
        b.get(rightOperand).demoteAll(t.pivot);
    }



    public void demoteBucket(String bucketId, String operator, String leftOperand, String rightOperand) throws Exception {
        assertDeclared(bucketId, rightOperand);
        OperatorObject op = opFunc.get(operator);
        if( op == null )
            throw new NotImplementedException();

        EntityResult t = new EntityResult();
        Bucket b = bucketList.get(bucketId);
        op.func.call(b, leftOperand, rightOperand, t);

        /// below lines would need refactoring as they may never be executed

        Entity rightE = b.get(rightOperand);
        Entity leftE = b.get(leftOperand);
        setGroupId(t, b.get(rightOperand));

        if(isDefined(b, leftOperand))
            leftE.demoteAll(t.aux);
        rightE.demoteAll(t.pivot);
    }

    public void resolveBucket(String bucketId) throws Exception {
        assertDeclared(bucketId);

        Bucket b = bucketList.get(bucketId);
        for(int pocket = 0; pocket <= b.getPocket(); pocket++) {
            boolean allHaveIt = true;
            for(String entityId: b.keySet()) {
                allHaveIt = allHaveIt && b.get(entityId).hasPocket(pocket);
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
            printBucket(objectId); //System.out.println(objectId + ":\n" +bucketList.get(objectId));
        else if(objectId.contains(".")) {
            String[] vars =  objectId.split(".");
            String entity = vars.length > 1 ? vars[1] : "";
            if( declaredVariables.containsKey(vars[0]) && declaredVariables.containsKey(entity))
                System.out.println(bucketList.get(vars[0]).get(vars[1]));
        }
    }

    public void printBucket(String bucketId) {
        if( !bucketList.containsKey(bucketId) ) return;
        System.out.println("Bucket " + bucketId);
        Bucket b = bucketList.get(bucketId);
        for(String eKey: b.keySet()){
            System.out.print("\tEntity " + eKey + ": ");
            for(CNode c: b.get(eKey)) {
                System.out.print(dsmQuery.GetType(c.classId) + "(" + c.pocket + ")" + ", ");
            }
            System.out.println();
        }
        System.out.println("\n---------------------------\n");
    }

    public void printByPocket(String bucketId) {
        if( !bucketList.containsKey(bucketId) ) return;
        System.out.println("Bucket " + bucketId);

        class PocketV {
            int pocketId = -1;
            double score = 0;
            String str = "";

            public PocketV(int p, int s, String print){
                pocketId = p; score = s; str = print;
            }
        }

        List<PocketV> pockets = new ArrayList<>();

        Bucket b = bucketList.get(bucketId);
        for(int i = 0; i <= b.getPocket(); i++) {
            if( !b.isPocketInAnyEntity(i) ) continue;
            String pocketStr = "";
            int score = 0;
            for(String eKey: b.keySet()){
                pocketStr += ("\tEntity " + eKey + ": ");
                double localScore = 0;
                int acceptedClasses = 0;
                Entity entity = b.get(eKey);
                for(CNode c: entity) {
                    if(c.pocket == i) {
                        pocketStr += (dsmQuery.GetType(c.classId) + "(" + c.pocket + ")" + ", ");
                        localScore += c.score;
                        ++acceptedClasses;
                    }
                }
                //localScore = localScore / (entity.getMaxScore() * acceptedClasses);
                score += localScore;
                pocketStr += "\n";
            }
            PocketV pocketV = new PocketV(i, score, pocketStr);
            pockets.add(pocketV);
        }

        pockets.sort(Comparator.comparing(p -> p.score));
        Collections.reverse(pockets);

        for(PocketV p: pockets) {
            System.out.print(p.str);
            System.out.print("\tscore: " + p.score + "\n\n");
        }
        System.out.println("\n---------------------------\n");
    }

    private void setGroupId(EntityResult t, Entity entity) {
        // t.pivot is the pivot by default
        for(CNode cn: t.pivot) {
            if(entity.hasClass(cn.classId)) {
                int existingPocket = entity.getByClassId(cn.classId).pocket;
                for(int i = 0; i < t.aux.size(); i++){
                    if(t.aux.get(i).pocket == cn.pocket){
                        t.aux.get(i).pocket = existingPocket;
                    }
                }
                cn.pocket = existingPocket;
            }
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

    private boolean isDefined(Bucket b, String... variableIds) {
        return b.keySet().containsAll(Arrays.asList(variableIds));
    }
}
