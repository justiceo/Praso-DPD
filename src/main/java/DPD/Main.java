package DPD;

import DPD.DSMMapper.*;
import DPD.DependencyBrowser.DSMBrowser;
import DPD.DependencyBrowser.IBrowser;
import DPD.PatternParser.CommonPatternsParser;
import DPD.PatternParser.IPatternsParser;
import DPD.PreProcessor.DSMPreprocessor;
import DPD.SourceParser.ASTAnalyzer;
import DPD.SourceParser.JParser;

import java.io.File;
import java.util.*;

/**
 * Created by Justice on 2/2/2016.
 */
public class Main {

    private RuleFilters ruleFilters;
    private EntityMapper mapper;
    private IBrowser browser;
    private IPattern pattern;
    private final String configFile = "config.xml";
    private final String testDsmFile = "files\\dsm\\simpleObserverPattern.dsm";
    private ILogger logger;

    public static void main(String[] args) {
        Main dpd = new Main();
        dpd.testIdm();
        dpd.analyze();
    }

    public void testIdm() {
        DSMPreprocessor preprocessor = new DSMPreprocessor();
        if(preprocessor.load(testDsmFile)) try {
            preprocessor.buildJClasses();
        } catch (InterruptedException e) {
            System.out.println("unable to load " + testDsmFile);
        }
    }

    public void analyze() {;

        logger = new ConsoleLogger();
        logger.setVerbose(false);

        IPatternsParser patternsParser = new CommonPatternsParser();
        patternsParser.init(new File(configFile));
        pattern = patternsParser.loadPatternById("observer1");


        File dsmFile = new File(testDsmFile);
        browser = new DSMBrowser(logger);
        browser.init(dsmFile);

        ASTAnalyzer sourceParser = new JParser(logger);

        mapper = new EntityMapper(browser, logger);
        mapper.mapPatternEntities(pattern);

        ruleFilters = new RuleFilters(browser, logger);
        ruleFilters.addSourceParser(sourceParser);

        // run filters through entities
        for(PatternRule rule: pattern.getRules()) {
            ruleFilters.filter(pattern, rule);
        }

        // resolve patterns
        List<IPattern> resolved = new ArrayList<>();
        for(PatternResolver resolver: pattern.getResolvers()) {
            resolved.addAll(ruleFilters.resolve(pattern, resolver));
            System.out.println("\ntotal patterns added: " + resolved.size());
        }

        // run ast
        for(IPattern pattern: resolved) {
            for (PatternRule rule : pattern.getRules()) {
                ruleFilters.checkSource(pattern, rule);
            }
        }

        // remove empty pattern
        Iterator<IPattern> pIterator = resolved.iterator();
        while(pIterator.hasNext()){
            IPattern pattern = pIterator.next();
            if(pattern.isVoid()) {
                pIterator.remove();
            }
        }

        // print out remaining ones
        for(IPattern pattern: resolved) {
            pattern.displayMembers(logger, browser);
        }

        // todo: convert this for-loop chain to a pipe
    }
}
