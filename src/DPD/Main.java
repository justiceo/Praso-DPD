package DPD;

import DPD.DSMMapper.*;
import DPD.DependencyBrowser.DSMBrowser;
import DPD.DependencyBrowser.IBrowser;
import DPD.PatternParser.CommonPatternsParser;
import DPD.PatternParser.IPatternsParser;
import DPD.SourceParser.ASTAnalyzer;
import DPD.SourceParser.JParser;

import java.io.File;
import java.util.List;

/**
 * Created by Justice on 2/2/2016.
 */
public class Main {

    private RuleFilters ruleFilters;
    private EntityMapper mapper;
    private IBrowser browser;
    private IPattern pattern;
    private final String configFile = "D:\\Code\\IdeaProjects\\DesignPatterns\\config.xml";
    private final String testDsmFile = "D:\\Code\\IdeaProjects\\DesignPatterns\\files\\ObserverPattern_2.dsm";

    public static void main(String[] args) {
        Main dpd = new Main();
        dpd.analyze();
    }

    public void analyze() {

        IPatternsParser patternsParser = new CommonPatternsParser();
        patternsParser.init(new File(configFile));
        pattern = patternsParser.loadPatternById("observer1");


        File dsmFile = new File(testDsmFile);
        browser = new DSMBrowser();
        browser.init(dsmFile);

        ASTAnalyzer sourceParser = new JParser();

        mapper = new DSMMapper();
        mapper.init(browser);
        mapper.mapPatternEntities(pattern);

        ruleFilters = new RuleFilters(browser);
        ruleFilters.addSourceParser(sourceParser);

        for(PatternRule rule: pattern.getRules()) {
            ruleFilters.filter(pattern, rule);
        }

        /*for(PatternResolver resolver: pattern.getResolvers()) {
            List<IPattern> resolved = ruleFilters.resolve(pattern, resolver);
            System.out.println("total patterns: " + resolved.size());
            resolved.forEach(p -> p.displayMembers(new ConsoleLogger()));
        }*/

        for(PatternRule rule: pattern.getRules()) {
            ruleFilters.checkSource(pattern, rule);
        }

        for(PatternResolver resolver: pattern.getResolvers()) {
            List<IPattern> resolved = ruleFilters.resolve(pattern, resolver);
            System.out.println("total patterns: " + resolved.size());
            resolved.forEach(p -> p.displayMembers(new ConsoleLogger()));
        }
    }
}
