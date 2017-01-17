package DPD.REPL;

import DPD.Model.Bucket;
import DPD.Model.CNode;
import DPD.Model.Entity;
import DPD.Model.Tuple;

import java.util.HashMap;

/**
 * Created by I853985 on 1/13/2017.
 */
public class OperatorFunctions extends HashMap<String, OperatorObject> {

    public OperatorFunctions(){
        put("and", new OperatorObject(true, (b, leftOp, rightOp, t) -> and_function(b, leftOp, rightOp, t)));
        put("method_name", new OperatorObject(false, OperatorFunctions::method_name_function));
        put("pocket_size", new OperatorObject(true, OperatorFunctions::pocket_size_function));
    }

    public OperatorObject get(String operator) {
        return super.get(operator.toLowerCase());
    }

    /**
     * Returns all classes in the left entity that also exist in the right entity
     * Hence, right entity tuple is always empty
     * @param b
     * @param leftOp
     * @param rightOp
     * @param t
     */
    private static void and_function(Bucket b, String leftOp, String rightOp, Tuple t) {
        // assert declared leftOp, and rightOp
        Entity left = b.get(leftOp);
        Entity right = b.get(rightOp);

        for(CNode c: left) {
            if(right.hasClass(c.classId))
                t.X.add(c);
        }
    }

    private static void method_name_function(Bucket b, String leftOp, String rightOp, Tuple t) {

    }

    /**
     * Returns all classes in the entity whose pocket size matches the leftOperand
     * @param b
     * @param leftOp
     * @param rightOp
     * @param t
     */
    private static void pocket_size_function(Bucket b, String leftOp, String rightOp, Tuple t) {
        int count = Integer.parseInt(leftOp);
        Entity target = b.get(rightOp);
        if(target.size() < count) return;
        HashMap<Integer, Integer> pocketCounter = new HashMap<>();
        for(CNode c: target) {
            int n = 0;
            if(pocketCounter.keySet().contains(c.pocket))
                n = pocketCounter.get(c.pocket);
            pocketCounter.put(c.pocket, ++n);
        }

        for(CNode c: target) {
            if(pocketCounter.get(c.pocket) == count)
                t.X.add(c);
        }

        target.removeByClassId(t.X);
    }

}
