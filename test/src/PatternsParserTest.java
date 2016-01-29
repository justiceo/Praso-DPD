import DPD.DSMMapper.IPattern;
import DPD.PatternParser.*;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Justice on 1/27/2016.
 */
public class PatternsParserTest { // just confirms we can load patterns from xml into IPatternObjects

    private IPatternsParser patternsParser;

    @Before
    public void setup() {
        patternsParser = new CommonPatternsParser();
        File configFile = new File("config.xml");
        patternsParser.init(configFile);
    }

    @After
    public void tearDown() {
        patternsParser = null;
    }

    @Test
    public void TestLoadConfig() {
        Config config = new Config();
        config.patterns = new ArrayList<>();
        PatternConfig observer = new PatternConfig();
        observer.name = "true";
        observer.include = true;
        observer.configFile = "trash";
        config.patterns.add(observer);
        config.patterns.add(observer);
        FileIO.saveConfig(config);
    }

    @Test
    public void TestPatternConfigEquals() {
        PatternConfig strategyConfig = new PatternConfig();
        strategyConfig.name = "Strategy Pattern";
        strategyConfig.include = true;
        strategyConfig.configFile = "strategy-pattern.xml";
        Assert.assertTrue(strategyConfig.equals(strategyConfig));
    }

    @Test
    public void TestGetPatternConfigs() {
        List<PatternConfig> expectedPatternConfigs = new ArrayList<>();


        PatternConfig observerConfig = new PatternConfig();
        observerConfig.name = "Observer Pattern";
        observerConfig.include = true;
        observerConfig.configFile = "observer-pattern.xml";

        PatternConfig strategyConfig = new PatternConfig();
        strategyConfig.name = "Strategy Pattern";
        strategyConfig.include = false;
        strategyConfig.configFile = "strategy-pattern.xml";

        expectedPatternConfigs.add(observerConfig);
        expectedPatternConfigs.add(strategyConfig);

        List<PatternConfig> actualPatternConfigs = patternsParser.getPatternConfigs();
        Assert.assertTrue(PatternConfig.compareList(expectedPatternConfigs, actualPatternConfigs));
    }

    @Test
    public void TestGetRunnablePatternConfigs() {
        List<PatternConfig> expectedPatternConfigs = new ArrayList<>();

        PatternConfig observerConfig = new PatternConfig();
        observerConfig.name = "Observer Pattern";
        observerConfig.include = true;
        observerConfig.configFile = "observer-pattern.xml";

        expectedPatternConfigs.add(observerConfig);

        List<PatternConfig> actualPatternConfigs = patternsParser.getRunnableConfigs();

        Assert.assertEquals(expectedPatternConfigs, actualPatternConfigs);
    }

    @Test
    public void TestParseConfig() {
        IPattern actualObserverPattern = patternsParser.parse(patternsParser.getRunnableConfigs().get(0));
        Assert.assertEquals("Observer Pattern", actualObserverPattern.getName());
        Assert.assertEquals(3, actualObserverPattern.getEntities().size());
        Assert.assertEquals(6, actualObserverPattern.getRules().size());
    }
}
