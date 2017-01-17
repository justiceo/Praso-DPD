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

    public void initDefaults() {
        put("excludes", new OperatorObject(true, (b, leftOp, rightOp, t) -> excludes_function(b, leftOp, rightOp, t)));
        put("method_name", new OperatorObject(false, OperatorFunctions::method_name_function));
    }

    public OperatorObject get(String operator) {
        return super.get(operator.toLowerCase());
    }

    private static void excludes_function(Bucket b, String leftOp, String rightOp, Tuple t) {
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

}
