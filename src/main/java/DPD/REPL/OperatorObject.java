package DPD.REPL;

import DPD.Model.Bucket;
import DPD.Model.Tuple;

/**
 * Created by Justice on 1/17/2017.
 */
public class OperatorObject {
    public boolean isSingleOperator;
    public OperatorInterface func;

    public OperatorObject(boolean isSingleOperator, OperatorInterface func) {
        this.isSingleOperator = isSingleOperator;
        this.func = func;
    }

    @FunctionalInterface
    public interface OperatorInterface {
        void call(Bucket b, String leftOp, String rightOp, Tuple t);
    }
}
