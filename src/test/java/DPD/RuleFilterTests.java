package DPD;

import DPD.DSMMapper.*;
import DPD.DependencyBrowser.IBrowser;
import DPD.PatternParser.CommonPatternsParser;
import DPD.PatternParser.IPatternsParser;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.List;

/**
 * Created by Justice on 1/27/2016.
 */
public class RuleFilterTests {

    private RuleFilters ruleFilters;
    private EntityMapper mapper;
    private IBrowser browser;
    private IPattern observerPattern;
    private final String configFile = "D:\\Code\\IdeaProjects\\DesignPatterns\\config.xml";
    private final String testDsmFile = "D:\\Code\\IdeaProjects\\DesignPatterns\\files\\observer-sample.dsm";

    @Before
    public void setup() {
        IPatternsParser patternsParser = new CommonPatternsParser();
        patternsParser.init(new File(configFile));
        observerPattern = patternsParser.parse(patternsParser.getRunnableConfigs().get(0));

        /*
        File dsmFile = new File(testDsmFile);
        browser = new DSMBrowser();
        browser.init(dsmFile);

        mapper = new EntityMapper();
        mapper.init(browser);
        mapper.mapPatternEntities(observerPattern);

        ruleFilters = new RuleFilters(browser);
        */
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
        //todo: re-write test
        // formal approach, get list of rules from observerPattern
        // foreach rule, parse in the source, target and exclusion to ruleFilters.

        for(PatternRule rule: observerPattern.getRules()) {
            ruleFilters.filter(observerPattern, rule);
        }
        //observerPattern.displayMembers(new ConsoleLogger());

        // resolver
        // get subjectBucket
        // for each subject, create a new pattern object, with niceName appended, addAll compliance

        //todo: make exhaustive
        //todo: add exclude parameter
    }

    @Test
    public void TestFilter() {
        for(PatternRule rule: observerPattern.getRules()) {
            ruleFilters.filter(observerPattern, rule);
        }
        //observerPattern.displayMembers(new ConsoleLogger());

        System.out.println("\n\n=== Resolving ===\n\n");
        for(PatternResolver resolver: observerPattern.getResolvers()) {
            List<IPattern> resolved = ruleFilters.resolve(observerPattern, resolver);
            //resolved.forEach(p -> p.displayMembers(new ConsoleLogger()));
        }
    }

    private IPattern filter(IPattern pattern) {
        for(PatternRule rule: pattern.getRules()) {
            ruleFilters.filter(pattern, rule);
        }
        return pattern;
    }


}
