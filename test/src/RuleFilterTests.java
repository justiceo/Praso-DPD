import DPD.ConsoleLogger;
import DPD.DSMMapper.*;
import DPD.DependencyBrowser.DSMBrowser;
import DPD.DependencyBrowser.DependencyType;
import DPD.DependencyBrowser.IBrowser;
import DPD.PatternParser.CommonPatternsParser;
import DPD.PatternParser.IPatternsParser;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

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

        File dsmFile = new File("D:\\Code\\IdeaProjects\\DesignPatterns\\test\\jhotdraw.dsm");
        System.out.println(dsmFile.exists());
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

        //todo: re-write test
        // formal approach, get list of rules from observerPattern
        // foreach rule, parse in the source, target and exclusion to ruleFilters.
        /*assertTrue(ruleFilters.filterIsAssociatedWithDependency(observerPattern, observerBucket, DependencyType.TYPED));
        assertTrue(ruleFilters.filterIsAssociatedWithDependency(observerPattern, observerBucket, DependencyType.USE));
        assertTrue(ruleFilters.filterImplements(observerPattern, concreteObserverBucket,                ));
        assertTrue(ruleFilters.filterUses(observerPattern, subjectBucket, observerBucket));
        assertTrue(ruleFilters.filterTypes(observerPattern, subjectBucket, observerBucket));*/
        for(PatternRule rule: observerPattern.getRules()) {
            ruleFilters.filter(observerPattern, rule);
        }
        //assertTrue(ruleFilters.filterIsPlural(observerPattern, concreteObserverBucket)); // run plural lasts!
        observerPattern.displayMembers(new ConsoleLogger());

        // resolver
        // get subjectBucket
        // for each subject, create a new pattern object, with niceName appended, addAll compliance

        //todo: make exhaustive
        //todo: add exclude parameter
    }

    @Test
    public void TestFilter() {
        // TestRule(EntityBuckets, Rule) : boolean
        // returns true if entityBuckets passes rules, i.e. affected buckets are not empty.
        //ruleFilters.filter(observerPattern);
    }

    private IPattern filter(IPattern pattern) {
        for(PatternRule rule: pattern.getRules()) {
            ruleFilters.filter(pattern, rule);
        }
        return pattern;
    }


}
