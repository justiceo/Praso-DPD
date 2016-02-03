import DPD.ConsoleLogger;
import DPD.DSMMapper.*;
import DPD.DependencyBrowser.DSMBrowser;
import DPD.DependencyBrowser.IBrowser;
import DPD.PatternParser.CommonPatternsParser;
import DPD.PatternParser.IPatternsParser;

import java.io.File;
import java.util.List;

/**
 * Created by Justice on 2/2/2016.
 */
public class Main {

    private RuleFilters ruleFilters;
    private EntityMapper mapper;
    private IBrowser browser;
    private IPattern observerPattern;
    private final String configFile = "D:\\Code\\IdeaProjects\\DesignPatterns\\config.xml";
    private final String testDsmFile = "D:\\Code\\IdeaProjects\\DesignPatterns\\files\\jhotdraw.dsm";

    public static void main(String[] args) {
        Main dpd = new Main();
        dpd.analyze();
    }

    public void analyze() {

        IPatternsParser patternsParser = new CommonPatternsParser();
        patternsParser.init(new File(configFile));
        observerPattern = patternsParser.parse(patternsParser.getRunnableConfigs().get(0));


        File dsmFile = new File(testDsmFile);
        browser = new DSMBrowser();
        browser.init(dsmFile);

        mapper = new DSMMapper();
        mapper.init(browser);
        mapper.mapPatternEntities(observerPattern);

        ruleFilters = new RuleFilters(browser);

        for(PatternRule rule: observerPattern.getRules()) {
            ruleFilters.filter(observerPattern, rule);
        }

        for(PatternResolver resolver: observerPattern.getResolvers()) {
            List<IPattern> resolved = ruleFilters.resolve(observerPattern, resolver);
            resolved.forEach(p -> p.displayMembers(new ConsoleLogger()));
        }
    }
}
