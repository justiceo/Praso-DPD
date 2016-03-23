package DPD;

import DPD.DSMMapper.*;
import DPD.DependencyBrowser.IBrowser;
import DPD.DependencyBrowser.IDMBrowser;
import DPD.DependencyBrowser.JClass;
import DPD.PatternParser.CommonPatternsParser;
import DPD.PatternParser.IPatternsParser;
import DPD.PreProcessor.DSMPreprocessor;
import DPD.SourceParser.ASTAnalyzer;
import DPD.SourceParser.JParser;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Justice on 2/2/2016.
 */
public class Main {

    private RuleFilters ruleFilters;
    private EntityMapper mapper;
    private IBrowser browser;
    private IPattern pattern;
    private final String configFile = "config.xml";
    private final String testDsmFile = "files\\dsm\\redisson.dsm";
    private ILogger logger;

    public static void main(String[] args) throws FileNotFoundException {
        Main dpd = new Main();
        dpd.testIdm();
        dpd.analyze();
    }

    public void testIdm() throws FileNotFoundException {
        DSMPreprocessor preprocessor = new DSMPreprocessor();
        if(preprocessor.load(testDsmFile)) try {
            preprocessor.buildJClasses();
        } catch (InterruptedException e) {
            System.out.println("unable to load " + testDsmFile);
        }

        List<JClass> jClasses = preprocessor.getClassList();

        browser = new IDMBrowser(new ConsoleLogger(), preprocessor.getClassList(), preprocessor.getDependencyLine());
        //browser = new DSMBrowser(new ConsoleLogger(), testDsmFile);
    }

    public void analyze() {;

        logger = new ConsoleLogger();
        logger.setVerbose(false);

        IPatternsParser patternsParser = new CommonPatternsParser();
        patternsParser.init(new File(configFile));
        pattern = patternsParser.loadPatternById("observer1");


        //browser = new DSMBrowser(logger);

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
        /*
        for(IPattern pattern: resolved) {
            for (PatternRule rule : pattern.getRules()) {
                ruleFilters.checkSource(pattern, rule);
            }
        }*/

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
