package DPD;

import DPD.DSMMapper.PatternComponent;
import DPD.DSMMapper.PatternDetector;
import DPD.DependencyBrowser.IBrowser;
import DPD.DependencyBrowser.IDMBrowser;
import DPD.PatternParser.CommonPatternsParser;
import DPD.PatternParser.IPatternsParser;
import DPD.PreProcessor.DSMPreprocessor;
import DPD.SourceParser.ASTAnalyzer;
import DPD.SourceParser.JParser;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * Created by Justice on 2/2/2016.
 */
public class Main {

    private static final String configFile = "config.xml";
    private static final String testDsmFile = "files\\dsm\\redisson.dsm";

    public static void main(String[] args) throws InterruptedException {
        DSMPreprocessor preprocessor = new DSMPreprocessor();
        try {
            if(preprocessor.load(testDsmFile)) {
                preprocessor.buildJClasses();
            }
        }
        catch (FileNotFoundException | InterruptedException e1) {
            System.out.println("error loading " + testDsmFile + ": " + e1.toString());
            System.exit(0);
        }

        IBrowser browser = new IDMBrowser(new ConsoleLogger(), preprocessor.getClassList(), preprocessor.getDependencyLine());

        ILogger logger = new ConsoleLogger();
        logger.setVerbose(false);

        IPatternsParser patternsParser = new CommonPatternsParser();
        patternsParser.init(new File(configFile));

        PatternComponent pattern = patternsParser.loadPatternById("observer1");

        ASTAnalyzer sourceParser = new JParser(logger);

        PatternDetector patternDetector = new PatternDetector(browser, pattern, logger);
        patternDetector.addSourceParser(sourceParser);
        Thread detectorT = new Thread(patternDetector);
        detectorT.start();

        detectorT.join();
        for(PatternComponent pattern1: patternDetector.resolvedPatterns) {
            pattern1.displayMembers(logger, browser);
        }
    }
}
