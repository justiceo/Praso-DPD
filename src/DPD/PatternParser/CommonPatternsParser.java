package DPD.PatternParser;
import DPD.DSMMapper.IPattern;

import java.io.File;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Justice on 1/27/2016.
 */
public class CommonPatternsParser implements IPatternsParser {
    private List<PatternConfig> patternConfigs;

    @Override
    public void init(File configFile) {
        Config config = FileIO.loadConfig(configFile);
        this.patternConfigs = config.patterns;
    }

    @Override
    public List<PatternConfig> getPatternConfigs() {
        return patternConfigs;
    }

    @Override
    public List<PatternConfig> getRunnableConfigs() {
        List<PatternConfig> runnableConfigs = new LinkedList<>();
        patternConfigs.stream().filter(p -> p.include).forEach(u -> runnableConfigs.add(u));
        return runnableConfigs;
    }

    @Override
    public IPattern parse(PatternConfig observerConfig) {
        IPattern pattern = FileIO.loadPattern(new File(observerConfig.configFile));
        return pattern;
    }
}
