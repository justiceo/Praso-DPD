package DPD.REPL;

import DPD.Browser.EasyDSMQuery;
import DPD.Model.Bucket;
import DPD.Model.CNode;
import DPD.Model.Entity;
import DPD.Model.Tuple;

import java.util.HashMap;

/**
 * Created by I853985 on 1/13/2017.
 */
public class OperatorFunctions extends HashMap<String, OperatorObject> {

    private EasyDSMQuery dsmBrowser;
    public OperatorFunctions(){
        put("and", new OperatorObject(true, (b, leftOp, rightOp, t) -> and_function(b, leftOp, rightOp, t)));
        put("method_name", new OperatorObject(false, (b, leftOp, rightOp, t) -> method_name_function(b, leftOp, rightOp, t)));
        put("pocket_size", new OperatorObject(true, (b, leftOp, rightOp, t) -> pocket_size_function(b, leftOp, rightOp, t)));
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
    private void and_function(Bucket b, String leftOp, String rightOp, Tuple t) {
        // assert declared leftOp, and rightOp
        Entity left = b.get(leftOp);
        Entity right = b.get(rightOp);
        left.removeByClassId(right);

        // problem with the implementation above is that it cannot be used in fill, promote and demote.
        /*for(CNode c: left) {
            if(right.hasClass(c.classId))
                t.X.add(c);
        }*/
    }

    private void method_name_function(Bucket b, String leftOp, String rightOp, Tuple t) {
        // get all entities in the rightop
        String method = leftOp;
        Entity entity = b.get(rightOp);

        // for each class in entity, check if it has method
        for(CNode c: entity) {
            // if it does, add it to the tuple
            // get the FileNode of c,
            // get it's CU
            // traverse the CU for method declaration
        }
    }

    /**
     * Returns all classes in the entity whose pocket size matches the leftOperand
     * @param b
     * @param leftOp
     * @param rightOp
     * @param t
     */
    private void pocket_size_function(Bucket b, String leftOp, String rightOp, Tuple t) {
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
        t.X.clear();

        // again problem with above is that it cannot be used in fill, promote and demote
    }

}
