package DPD;

import java.util.Objects;

/**
 * Created by Justice on 1/28/2016.
 */
public interface ILogger {

    enum OutputLevel {
        log,
        info,
        detailedInfo
    }

    void log(Object obj);
    void setVerbose(boolean isVerbose);
    void info(Object obj);
    void detailedInfo(Object obj);
    void error(Object obj);
}
