package DPD.PatternParser;

import DPD.DSMMapper.IPattern;

import java.io.File;
import java.util.List;

/**
 * Created by Justice on 1/27/2016.
 */
public interface IPatternsParser {


    void init(File configFile);

    List<PatternConfig> getPatternConfigs();

    List<PatternConfig> getRunnableConfigs();

    IPattern parse(PatternConfig observerConfig);
}
