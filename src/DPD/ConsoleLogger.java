package DPD;

import DPD.ILogger;

/**
 * Created by Justice on 1/28/2016.
 */
public class ConsoleLogger implements ILogger {

    private boolean verbose;
    @Override
    public void log(Object obj) {
        System.out.println(obj);
    }

    @Override
    public void setVerbose(boolean isVerbose) {
        this.verbose = isVerbose;
    }

    @Override
    public void info(Object obj) {
        if(verbose)
            System.out.println(obj);
    }

    @Override
    public void detailedInfo(Object obj) {
        if(verbose)
            System.out.println(obj);
    }

    @Override
    public void error(Object obj) {
        System.out.println("***ERR: " + obj);
    }
}
