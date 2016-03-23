package DPD.PreProcessor;

import DPD.DependencyBrowser.JClass;

import java.util.List;

/**
 * Created by Justice on 3/17/2016.
 */
public abstract class Filter extends Thread {
    protected List<JClass> jClasses;
    protected int matrixSize;


    public void init(List<JClass> jClasses, int size) {
        this.jClasses = jClasses;
        this.matrixSize = size;
    }
}
