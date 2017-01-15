package DPD.REPL;

import DPD.Model.Bucket;
import DPD.Model.CNode;
import DPD.Model.Entity;
import DPD.Model.Tuple;
import jdk.internal.org.objectweb.asm.tree.ClassNode;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 * Created by I853985 on 1/13/2017.
 */
public class OperatorFunctions {

    private Excludes excludes;
    private MethodName methodName;

    public OperatorFunctions() {
        methodName = new MethodName();
        excludes = new Excludes();
    }

    public boolean isValidOperator(String operatorName) {
        for (Operator d : Operator.values()) {
            if (d.name().equals(operatorName.toUpperCase())) { // todo: testto upper
                return true;
            }
        }
        return false;
    }

    public void call(String operatorName, Bucket b, String leftOp, String rightOp, Tuple t) {
        Operator op = Operator.valueOf(operatorName.toUpperCase());
        switch (op) {
            case EXCLUDES:
                excludes.call(b, leftOp, rightOp);
                break;
            case METHOD_NAME:
                methodName.call(b, leftOp, rightOp);
                break;
            default:
                break;
        }

    }

    private enum Operator {
        EXCLUDES,
        METHOD_NAME
    }

    class Excludes implements IOperatorFunction {
        public void call(Bucket b,  String leftOp, String rightOp) {
            // assert declared leftOp, and rightOp
            Entity left = b.get(leftOp);
            Entity right = b.get(rightOp);
            left.removeByClassId(right);
        }
    }

    class MethodName implements IOperatorFunction {
        public void call(Bucket b, String leftOp, String rightOp) {
            throw new NotImplementedException();
        }
    }

    interface IOperatorFunction {
        void call(Bucket b, String leftOp, String rightOp);
    }
}
