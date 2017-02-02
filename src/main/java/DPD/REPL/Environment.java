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

    public BucketResult evalDependency(DependencyType dependency, String leftOperand, String rightOperand) throws Exception {
        return evalDependency(toList(dependency), leftOperand, rightOperand);
    }

    public BucketResult evalDependency(List<DependencyType> dependency, String leftOperand, String rightOperand) throws Exception {
        assertDeclared(leftOperand, rightOperand);
        BucketResult t = new BucketResult();
        dsmQuery.populate(dependency, t);

        t.put(leftOperand, t.aux);
        t.put(rightOperand, t.pivot);
        setGroupId(t, t.get(rightOperand));
        return t;
    }

    public BucketResult evalFunction(String bucketId, String operator, String... operands) throws Exception {
        OperatorObject op = opFunc.get(operator);
        if( op == null )
            throw new NotImplementedException();

        BucketResult bucketResult = new BucketResult();
        op.func.call(bucketList.get(bucketId), operands[0], operands[1], bucketResult);
        return bucketResult;
    }

    public void evalBucketStatement(String bucketId, Evaluator.StatementType action, BucketResult bResult) throws Exception {
        assertDeclared(bucketId);
        Bucket b = bucketList.get(bucketId);
        bResult.keySet().forEach(k -> b.addIfNotExists(k));
        switch (action) {
            case FillStatement:
                b.putAll(bResult); // this would replace
                break;
            case OverwriteStatement:
                // this has a filter effect, so elements should already exist in entity
                bResult = trimToMatchBucket(b, bResult);
                bResult.keySet().forEach(k -> b.get(k).clear());
                b.putAll(bResult);
                break;
            case FilterStatement:
                // this has a filter effect, so elements should already exist in entity
                bResult = trimToMatchBucket(b, bResult);
                BucketResult finalBResult = bResult;
                bResult.keySet().forEach(k -> b.get(k).removeByClassId(finalBResult.get(k)));
                break;
            case PromoteStatement:
                // necessary that we're promoting only items already in the entity
                bResult = trimToMatchBucket(b, bResult);
                finalBResult = bResult;
                bResult.keySet().forEach(k -> b.get(k).promoteAll(finalBResult.get(k)));
                break;
            case DemoteStatement:
                bResult = trimToMatchBucket(b, bResult);
                finalBResult = bResult;
                bResult.keySet().forEach(k -> b.get(k).promoteAll(finalBResult.get(k)));
                break;
        }
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

    private void setGroupId(BucketResult t, Entity entity) {
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

    public static <T> List<T> toList( T input ) {
        List<T> result = new ArrayList<T>();
        result.add(input);
        return result;
    }
	
	private BucketResult trimToMatchBucket(Bucket principal, BucketResult composite) {
		// for each entity in composite bucket, if it exists, replace otherwise remove
		for(String key: composite.keySet()){
			Entity pE = principal.get(key);
			Entity cE = composite.get(key);
			for(int i = 0; i < cE.size(); i++) {
				CNode cn = cE.get(i);
				if(pE.hasClass(cn.classId))
					cn = pE.getByClassId(cn.classId);
				else 
					cE.remove(cn);
			}
		}
		return composite;
	}

}
