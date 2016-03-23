package DPD;

import DPD.DSMMapper.PatternComponent;
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
    private final String configFilePath = "D:\\Code\\IdeaProjects\\DesignPatterns\\config.xml";
    private final int observer1RuleCount = 6;
    private final int observer1EntityCount = 3;
    private final String observer1Name = "Observer Pattern";
    private final String observer1Id = "observer1";
    private final String observer1ConfigFile = "D:\\Code\\IdeaProjects\\DesignPatterns\\files\\observer-pattern-v2.xml";

    @Before
    public void setup() {
        patternsParser = new CommonPatternsParser();
        File configFile = new File(configFilePath);
        patternsParser.init(configFile);
    }

    @After
    public void tearDown() {
        patternsParser = null;
    }

    @Test
    public void TestPatternConfigEquals() {
        PatternConfig strategyConfig = new PatternConfig();
        strategyConfig.id = "strategy1";
        strategyConfig.name = "Strategy Pattern";
        strategyConfig.include = true;
        strategyConfig.configFile = "strategy-pattern.xml";
        Assert.assertTrue(strategyConfig.equals(strategyConfig));
    }

    @Test
    public void TestGetPatternConfigs() {
        List<PatternConfig> expectedPatternConfigs = new ArrayList<>();

        PatternConfig observerConfig = new PatternConfig();
        observerConfig.id = observer1Id;
        observerConfig.name = observer1Name;
        observerConfig.include = true;
        observerConfig.configFile = observer1ConfigFile;

        PatternConfig strategyConfig = new PatternConfig();
        strategyConfig.id = "strategy1";
        strategyConfig.name = "Strategy Pattern";
        strategyConfig.include = false;
        strategyConfig.configFile = "D:\\Code\\IdeaProjects\\DesignPatterns\\files\\strategy-pattern.xml";

        expectedPatternConfigs.add(observerConfig);
        expectedPatternConfigs.add(strategyConfig);

        List<PatternConfig> actualPatternConfigs = patternsParser.getPatternConfigs();
        Assert.assertTrue(PatternConfig.compareList(expectedPatternConfigs, actualPatternConfigs));
    }

    @Test
    public void TestGetRunnablePatternConfigs() {
        List<PatternConfig> expectedPatternConfigs = new ArrayList<>();

        PatternConfig observerConfig = new PatternConfig();
        observerConfig.id = observer1Id;
        observerConfig.name = observer1Name;
        observerConfig.include = true;
        observerConfig.configFile = observer1ConfigFile;

        expectedPatternConfigs.add(observerConfig);

        List<PatternConfig> actualPatternConfigs = patternsParser.getRunnableConfigs();

        Assert.assertEquals(expectedPatternConfigs, actualPatternConfigs);
    }

    @Test
    public void TestParseConfig() {
        PatternComponent actualObserverPattern = patternsParser.parse(patternsParser.getRunnableConfigs().get(0));
        Assert.assertEquals(observer1Name, actualObserverPattern.getName());
        Assert.assertEquals(observer1EntityCount, actualObserverPattern.getEntities().size());
        Assert.assertEquals(observer1RuleCount, actualObserverPattern.getRules().size());
    }

    @Test
    public void TestLoadPatternById() {
        PatternComponent actualObserverPattern = patternsParser.loadPatternById(observer1Id);
        Assert.assertEquals(observer1Name, actualObserverPattern.getName());
        Assert.assertEquals(observer1EntityCount, actualObserverPattern.getEntities().size());
        Assert.assertEquals(observer1RuleCount, actualObserverPattern.getRules().size());
    }
}
