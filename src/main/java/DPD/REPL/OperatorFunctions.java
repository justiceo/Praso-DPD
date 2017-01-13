package DPD.REPL;

import DPD.Model.Bucket;
import DPD.Model.CNode;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.List;

/**
 * Created by I853985 on 1/13/2017.
 */
public class OperatorFunctions {

    private Excludes excludes;
    private MethodName methodName;

    public OperatorFunctions() {
        // might need a dsm browser, file browser etc
        //throw new NotImplementedException();
    }

    public boolean isValidOperator(String operatorName) {
        for (Operator d : Operator.values()) {
            if (d.name().equals(operatorName)) { // todo: testto upper
                return true;
            }
        }
        return false;
    }

    public void call(String operatorName, Bucket b, String leftOp, String rightOp, List<CNode> x, List<CNode> y) {
        Operator op = Operator.valueOf(operatorName);
        switch (op) {
            case Excludes:
                excludes.call(b, leftOp, rightOp);
                break;
            case Method_Name:
                methodName.call(b, leftOp, rightOp);
                break;
            default:
                break;
        }

    }

    private enum Operator {
        Excludes,
        Method_Name
    }

    class Excludes implements IOperatorFunction {
        public void call(Bucket b,  String leftOp, String rightOp) {
            throw new NotImplementedException();
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
