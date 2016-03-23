package DPD;

/**
 * Created by Justice on 1/28/2016.
 */
public interface ILogger {

    void log(Object obj);

    void setVerbose(boolean isVerbose);

    void info(Object obj);

    void detailedInfo(Object obj);

    void error(Object obj);

    enum OutputLevel {
        log,
        info,
        detailedInfo
    }
}
