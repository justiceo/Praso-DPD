package DPD;

import DPD.Browser.EasyDSMQuery;
import DPD.Model.Bucket;
import DPD.REPL.Environment;
import DPD.REPL.OperatorFunctions;
import DPD.REPL.Evaluator;

import java.lang.reflect.Field;
import java.util.HashMap;

/**
 * Created by I853985 on 1/13/2017.
 */
public class ExecEnvAccess {

    public HashMap<String, String> declaredVariables;
    public HashMap<String, Bucket> bucketList;
    public EasyDSMQuery dsmQuery;
    public OperatorFunctions opFunc;

    private String[] varNames = new String[] {"declaredVariables", "bucketList", "dsmQuery", "opFunc"};

    public ExecEnvAccess(Evaluator repl) throws Exception {
        init(getExecEnv(repl));
    }

    public ExecEnvAccess(Environment exec) throws Exception {
        init(exec);
    }

    private void init(Environment exec) throws Exception {
        Field field = Environment.class.getDeclaredField(varNames[0]);
        field.setAccessible(true);
        declaredVariables = (HashMap<String, String>) field.get(exec);

        field = Environment.class.getDeclaredField(varNames[1]);
        field.setAccessible(true);
        bucketList = (HashMap<String, Bucket>) field.get(exec);

        field = Environment.class.getDeclaredField(varNames[2]);
        field.setAccessible(true);
        dsmQuery = (EasyDSMQuery) field.get(exec);

        field = Environment.class.getDeclaredField(varNames[3]);
        field.setAccessible(true);
        opFunc = (OperatorFunctions) field.get(exec);
    }

    public static Environment getExecEnv(Evaluator repl) throws NoSuchFieldException, IllegalAccessException {
        String execEnv = "exec";
        Field field = Evaluator.class.getDeclaredField(execEnv);
        field.setAccessible(true);
        return (Environment) field.get(repl);
    }
}
