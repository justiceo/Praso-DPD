package DPD.PreProcessor;

import DPD.DependencyBrowser.JClass;

import java.util.List;

/**
 * Created by Justice on 3/17/2016.
 */
public abstract class Filter extends Thread {
    protected static List<JClass> jClasses;
    protected static int matrixSize;


    public static void init(List<JClass> jClass, int size) {
        jClasses = jClass;
        matrixSize = size;
    }
}
