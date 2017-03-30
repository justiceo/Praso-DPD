package DPD.REPL;

import DPD.Browser.EasyDSMQuery;
import DPD.Model.*;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.*;
import java.util.stream.Collectors;

import static DPD.Util.isInteger;
import static DPD.Util.println;

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
        BucketResult t = dsmQuery.populate(dependency);

        t.put(leftOperand, t.aux);
        t.put(rightOperand, t.pivot);
        // no access to bucket means this cannot happen here;
        //setGroupId(t, t.get(rightOperand));
        return t;
    }

    public BucketResult evalFunction(String bucketId, String operator, String... operands) throws Exception {
        return evalFunction(bucketList.get(bucketId), operator, operands);
    }

    public BucketResult evalFunction(Bucket bucket, String operator, String... operands) throws Exception {
        OperatorObject op = opFunc.get(operator);
        if( op == null )
            throw new NotImplementedException();

        return op.func.call(bucket, operands[0], operands[1]);
    }

    public Bucket evalBucketStatement(String bucketId, Evaluator.StatementType action, BucketResult bResult, String pivot) throws Exception {
        assertDeclared(bucketId);
        Bucket b = bucketList.get(bucketId);
        if(!pivot.isEmpty())
            setGroupId(bResult, b.get(pivot), pivot);
        return evalBucketStatement(b, action, bResult);
    }

    public static Bucket evalBucketStatement(Bucket b, Evaluator.StatementType action, BucketResult bResult) throws Exception {
        bResult.keySet().forEach(k -> b.addIfNotExists(k));
        switch (action) {
            case FillStatement:
                BucketResult finalBResult1 = bResult;
                bResult.keySet().forEach(k -> b.get(k).addAll(finalBResult1.get(k).toList()));
                break;
            case OverwriteStatement:
                // this has a filter effect, so elements should already exist in entity
                bResult = trimToMatchBucket(b, bResult);
                bResult = resolveBucket(bResult);
                for(String k: bResult.keySet()) {
                    b.get(k).clear();
                    b.get(k).addAll(bResult.get(k).toList());
                }
                break;
            case FilterStatement:
                // this has a filter effect, so elements should already exist in entity
                bResult = trimToMatchBucket(b, bResult);
                bResult = resolveBucket(bResult);
                BucketResult finalBResult = bResult;
                bResult.keySet().forEach(k -> b.get(k).removeAll(finalBResult.get(k).toList()));
                break;
            case PromoteStatement:
                // necessary that we're promoting only items already in the entity
                bResult = trimToMatchBucket(b, bResult);
                bResult = resolveBucket(bResult);
                finalBResult = bResult;
                for(String k: bResult.keySet())
                    b.get(k).promoteAll(bResult.get(k).toList());
                //bResult.keySet().forEach(k -> b.get(k).promoteAll(finalBResult.get(k).toList()));
                break;
            case DemoteStatement:
                bResult = trimToMatchBucket(b, bResult);
                bResult = resolveBucket(bResult);
                finalBResult = bResult;
                bResult.keySet().forEach(k -> b.get(k).demoteAll(finalBResult.get(k).toList()));
                break;
        }
        return b;
    }

    public Bucket resolveBucket(String bucketId) throws Exception {
        assertDeclared(bucketId);

        Bucket b = bucketList.get(bucketId);
        return resolveBucket(b);
    }

    public static Bucket resolveBucket(Bucket b) throws Exception {
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
        return b;
    }


    public static BucketResult resolveBucket(BucketResult b) throws Exception {
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
        return b;
    }

    /**
     * Prints bucket and bucket entities if specified
     * input types bucket or bucket.entity
     * @param objectId
     * @throws Exception
     */
    public void printObject(String objectId) throws Exception {
        if(bucketList.containsKey(objectId)) {
            System.out.println("Bucket " + objectId);
            printBucket(bucketList.get(objectId));
        }
        else if(objectId.contains(".")) {
            String[] vars =  objectId.split(".");
            String entity = vars.length > 1 ? vars[1] : "";
            if( declaredVariables.containsKey(vars[0]) && declaredVariables.containsKey(entity))
                System.out.println(bucketList.get(vars[0]).get(vars[1]));
        }
    }

    public void printBucket(Bucket b) {
        double pocketScore = 0;
        for(String eKey: b.keySet()){
            System.out.print("\tEntity " + eKey + ": ");
            pocketScore += b.get(eKey).getMaxScore();
            for(CNode c: b.get(eKey).toList()) {
                System.out.print(dsmQuery.GetType(c.classId) + "(" + c.classId + "," + c.pocket + ")" + ", ");
            }
            System.out.println();
        }
        System.out.print("\tscore: " + pocketScore  + "\n\n");
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
                for(CNode c: entity.toList()) {
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

    private void setGroupId(BucketResult t, Entity entity, String pivot) {
        // should be bResult.get(pivot)
        // t.pivot is the pivot by default
        Entity pivotE;
        if(t.containsKey(pivot))
            pivotE = t.getEntity(pivot);
        else
            pivotE = t.pivot;
        for(CNode cn: pivotE.toList()) {
            if(entity.hasClass(cn.classId)) {
                int existingPocket = entity.get(cn.classId).pocket;
                for(int i: t.aux.keySet()){
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

    // removes every class or entity in composite that is not in principal
    // hence we're are trimming composite contents to match the elements in principal
	public static BucketResult trimToMatchBucket(Bucket principal, BucketResult composite) {
		// for each entity in composite bucket, if it exists, replace otherwise remove
        BucketResult result = new BucketResult();
		for(String key: composite.keySet()){
			Entity pE = principal.get(key);
            if(pE == null) { // entity doesn't exist, so we don't have to add it's classes
                continue;
            }
			Entity cE = composite.get(key);
            Entity e = new Entity();
            for(CNode cn: cE.toList()) {
                if(pE.hasClass(cn.classId)) {
                    e.add(pE.get(cn.classId).cloneTo(cn));
                }
            }
            result.put(key, e);
		}
		return result;
	}

    public void getSubDsm(String typeOrClassIndex) {
        // if it's all numbers, then it's class Id, check if it exists
        // if type isn't valid, print potential alternates
        if(isInteger(typeOrClassIndex)) {
            int index = Integer.parseInt(typeOrClassIndex);
            println(dsmQuery.getSubDSM(index));
        }
        else {
            println(dsmQuery.getSubDSM(typeOrClassIndex));
        }
    }

    public void getSubDsm(String[] args) {
        println(dsmQuery.getSubDSM(args));
    }

    public void printDsm() {
        println(dsmQuery);
    }

    public void findTypes(String query) {
        String finalQuery = query.toLowerCase();
        List<String> paths  = dsmQuery.getTypes().stream()
                .filter(ff -> ff.toLowerCase().contains(finalQuery))
                .collect(Collectors.toList());
        println(paths);
    }

    public void getClassId(String type) {
        println(dsmQuery.getClassId(type));
    }

    public void export(String[] type) {

        try{
            // Create file
            FileWriter fstream = new FileWriter("export-dsm.txt");
            BufferedWriter out = new BufferedWriter(fstream);
            out.write(dsmQuery.getSubDSM(type).toString());
            //Close the output stream
            out.close();
        }catch (Exception e){//Catch exception if any
            System.err.println("Error: " + e.getMessage());
        }
    }
}
