package DPD.REPL;

import DPD.Browser.EasyDSMQuery;
import DPD.Model.*;
import DPD.Util;
import com.github.javaparser.ast.CompilationUnit;

import java.util.HashMap;

/**
 * Created by I853985 on 1/13/2017.
 */
public class OperatorFunctions extends HashMap<String, OperatorObject> {

    private EasyDSMQuery browser;
    public OperatorFunctions(EasyDSMQuery dsmBrowser){
        put("and", new OperatorObject(true, (b, leftOp, rightOp, t) -> and_function(b, leftOp, rightOp, t)));
        put("method_name", new OperatorObject(false, this::method_name_function));
        put("pocket_size", new OperatorObject(true, (b, leftOp, rightOp, t) -> pocket_size_function(b, leftOp, rightOp, t)));
        put("min_pocket_size", new OperatorObject(true, (b, leftOp, rightOp, t) -> min_pocket_size_function(b, leftOp, rightOp, t)));
        this.browser = dsmBrowser;
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

        for(CNode c: left) {
            if(right.hasClass(c.classId))
                t.X.add(c);
        }
    }

    private void method_name_function(Bucket b, String leftOp, String rightOp, Tuple t) throws Exception {
        Entity entity = b.get(rightOp);
        String[] args = Util.extractArray(leftOp);

        // for each class in entity, check if it has method
        for(CNode c: entity) {
            FileNode fn = browser.getFileNode(c.classId);
            CompilationUnit cu = fn.getCu();
            MethodNameVisitor mv = new MethodNameVisitor();
            if(mv.hasMethodName(cu, args))
                t.Y.add(c);
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
                t.Y.add(c);
        }
    }

    /**
     * Returns all nodes whose pocket size is higher than or equal to the value specified
     * @param b
     * @param leftOp
     * @param rightOp
     * @param t
     */
    private void min_pocket_size_function(Bucket b, String leftOp, String rightOp, Tuple t) {
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
            if(pocketCounter.get(c.pocket) >= count)
                t.Y.add(c);
        }
    }

}
