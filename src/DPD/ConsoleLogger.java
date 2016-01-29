package DPD;

import DPD.ILogger;

/**
 * Created by Justice on 1/28/2016.
 */
public class ConsoleLogger implements ILogger {
    @Override
    public void log(Object obj) {
        System.out.println(obj);
    }
}
