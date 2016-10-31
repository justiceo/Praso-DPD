package DPD;

import DPD.DSMMapper.PatternComponent;
import DPD.DSMMapper.PatternDetectorManager;
import DPD.DependencyBrowser.IBrowser;
import DPD.DependencyBrowser.IDMBrowser;
import DPD.PatternParser.CommonPatternsParser;
import DPD.PatternParser.IPatternsParser;
import DPD.PatternParser.PatternConfig;
import DPD.PreProcessor.DSMPreprocessor;
import DPD.SourceParser.JParser;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Justice on 2/2/2016.
 */
public class Main {

    private static final String configFile = "config.xml";
    private static final String testDsmFile = "files\\dsm\\Maze.dsm";

    public static void main(String[] args) throws InterruptedException {
        DSMPreprocessor preprocessor = new DSMPreprocessor();
        try {
            if (preprocessor.load(testDsmFile)) {
                preprocessor.buildJClasses();
            }
        } catch (FileNotFoundException | InterruptedException e1) {
            System.out.println("error loading " + testDsmFile + ": " + e1.toString());
            System.exit(0);
        }

        IBrowser browser = new IDMBrowser(new ConsoleLogger(), preprocessor.getClassList(), preprocessor.getDependencyLine());

        ILogger logger = new ConsoleLogger();
        logger.setVerbose(false);

        IPatternsParser patternsParser = new CommonPatternsParser();
        patternsParser.init(new File(configFile));

        JParser sourceParser = new JParser(logger);

        List<PatternComponent> patternComponentList = new LinkedList<>(); // variations are loaded as separate patterns
        List<PatternConfig> configs = patternsParser.getRunnableConfigs();
        configs.stream().filter(config -> config.include).forEach(config -> {
            PatternComponent pc = patternsParser.loadPatternById(config.id);
            patternComponentList.add(pc);
        });
        System.out.println("number of patterns loaded: " + patternComponentList.size());

        PatternDetectorManager pdManager = new PatternDetectorManager(patternComponentList, browser, sourceParser);
        pdManager.startPDs();
        List<PatternComponent> resolvedPatterns = pdManager.getResults();

        // display patterns
        for (PatternComponent pattern : resolvedPatterns) {
            pattern.displayMembers(logger, browser);
        }
    }
}
