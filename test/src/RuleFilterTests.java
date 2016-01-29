import DPD.DSMBrowser.DSMBrowser;
import DPD.DSMBrowser.IBrowser;
import DPD.DSMMapper.DSMMapper;
import DPD.DSMMapper.EntityMapper;
import DPD.DSMMapper.IPattern;
import DPD.DSMMapper.RuleFilters;
import DPD.PatternParser.CommonPatternsParser;
import DPD.PatternParser.IPatternsParser;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by Justice on 1/27/2016.
 */
public class RuleFilterTests {

    private RuleFilters ruleFilters;
    private EntityMapper mapper;
    private IBrowser browser;
    private IPattern observerPattern;

    @Before
    public void setup() {
        IPatternsParser patternsParser = new CommonPatternsParser();
        patternsParser.init(new File("config.xml"));
        observerPattern = patternsParser.parse(patternsParser.getRunnableConfigs().get(0));

        File dsmFile = new File("observer-sample.dsm");
        browser = new DSMBrowser();
        browser.init(dsmFile);

        mapper = new DSMMapper();
        mapper.init(browser);
        mapper.mapPatternEntities(observerPattern);

        ruleFilters = new RuleFilters(browser);
    }

    @After
    public void teardown() {
        browser = null;
        mapper = null;
        observerPattern = null;
        ruleFilters = null;
    }

    @Test
    public void TestRules() {
        String observerBucket = observerPattern.getEntities().get(0).id;
        String concreteObserverBucket = observerPattern.getEntities().get(1).id;
        String subjectBucket = observerPattern.getEntities().get(2).id;

        assertTrue(ruleFilters.filterImplements(observerPattern, concreteObserverBucket, observerBucket));
        assertTrue(ruleFilters.filterUses(observerPattern, subjectBucket, observerBucket));
        assertTrue(ruleFilters.filterTypes(observerPattern, subjectBucket, observerBucket));
        assertTrue(ruleFilters.filterIsPlural(observerPattern, concreteObserverBucket)); // run plural lasts!

        // test adding include
    }

    @Test
    public void TestFilter() {
        // TestRule(EntityBuckets, Rule) : boolean
        // returns true if entityBuckets passes rules, i.e. affected buckets are not empty.
        ruleFilters.filter(observerPattern);
    }


}
