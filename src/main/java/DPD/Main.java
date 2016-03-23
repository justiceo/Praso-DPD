package DPD;

import DPD.DSMMapper.*;
import DPD.DependencyBrowser.IBrowser;
import DPD.DependencyBrowser.IDMBrowser;
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

    private PatternDetector patternDetector;
    private IBrowser browser;
    private PatternComponent pattern;
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

        patternDetector = new PatternDetector(browser, logger);
        patternDetector.mapPatternEntities(pattern);
        patternDetector.addSourceParser(sourceParser);

        // run filters through entities
        for(PatternRule rule: pattern.getRules()) {
            patternDetector.filter(pattern, rule);
        }

        // resolve patterns
        List<PatternComponent> resolved = new ArrayList<>();
        for(PatternResolver resolver: pattern.getResolvers()) {
            resolved.addAll(patternDetector.resolve(pattern, resolver));
            System.out.println("\ntotal patterns added: " + resolved.size());
        }

        // run ast

        for(PatternComponent pattern: resolved) {
            for (PatternRule rule : pattern.getRules()) {
                patternDetector.checkSource(pattern, rule);
            }
        }

        // remove empty pattern
        Iterator<PatternComponent> pIterator = resolved.iterator();
        while(pIterator.hasNext()){
            PatternComponent pattern = pIterator.next();
            if(pattern.isVoid()) {
                pIterator.remove();
            }
        }

        // print out remaining ones
        for(PatternComponent pattern: resolved) {
            pattern.displayMembers(logger, browser);
        }

        // todo: convert this for-loop chain to a pipe
    }
}
