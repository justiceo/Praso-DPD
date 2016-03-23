package DPD.PatternParser;

import DPD.DSMMapper.PatternComponent;

import java.io.File;
import java.util.List;

/**
 * Created by Justice on 1/27/2016.
 */
public interface IPatternsParser {


    void init(File configFile);

    List<PatternConfig> getPatternConfigs();

    List<PatternConfig> getRunnableConfigs();

    PatternComponent parse(PatternConfig observerConfig);

    PatternComponent loadPatternById(String patternId);
}
