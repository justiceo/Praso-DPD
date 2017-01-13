package DPD;

import DPD.Browser.EasyDSMQuery;
import DPD.Model.Bucket;
import DPD.REPL.ExecEnv;
import DPD.REPL.OperatorFunctions;
import DPD.REPL.REPL;

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

    public ExecEnvAccess(REPL repl) throws NoSuchFieldException, IllegalAccessException  {

        ExecEnv exec = getExecEnv(repl);

        Field field = ExecEnv.class.getDeclaredField(varNames[0]);
        field.setAccessible(true);
        declaredVariables = (HashMap<String, String>) field.get(exec);

        field = ExecEnv.class.getDeclaredField(varNames[1]);
        field.setAccessible(true);
        bucketList = (HashMap<String, Bucket>) field.get(exec);

        field = ExecEnv.class.getDeclaredField(varNames[2]);
        field.setAccessible(true);
        dsmQuery = (EasyDSMQuery) field.get(exec);

        field = ExecEnv.class.getDeclaredField(varNames[3]);
        field.setAccessible(true);
        opFunc = (OperatorFunctions) field.get(exec);
    }

    public static ExecEnv getExecEnv(REPL repl) throws NoSuchFieldException, IllegalAccessException {
        String execEnv = "exec";
        Field field = REPL.class.getDeclaredField(execEnv);
        field.setAccessible(true);
        return (ExecEnv) field.get(repl);
    }
}
