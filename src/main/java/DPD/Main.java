package DPD;

import DPD.DSMMapper.PatternComponent;
import DPD.DSMMapper.PatternDetectorManager;
import DPD.DependencyBrowser.DSMBrowser;
import DPD.DependencyBrowser.IDMBrowser;
import DPD.PatternParser.CommonPatternsParser;
import DPD.PatternParser.IDesignPatternRules;
import DPD.PatternParser.PatternConfig;
import DPD.PreProcessor.LoadDSM;
import DPD.SourceParser.JParser;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Justice on 2/2/2016.
 */
public class Main {

    private static final String configFile = "config.xml";
    private static final String testDsmFile = "files\\dsm\\simpleObserverPattern.dsm";

    public static void main(String[] args) throws InterruptedException {
        // tryLoad the dsm file and on the way processing the classes
        LoadDSM loadDSM = new LoadDSM();
        try {
            if (loadDSM.load(testDsmFile)) {
                loadDSM.buildJClasses();
            }
        } catch (InterruptedException e1) {
            System.out.println("error loading " + testDsmFile + ": " + e1.toString());
            System.exit(0);
        }

        // hydrates the fields in each jClass
        loadDSM.process();

        DSMBrowser browser = new IDMBrowser(new ConsoleLogger(), loadDSM.getClassList(), loadDSM.getDependencyLine());

        ILogger logger = new ConsoleLogger();
        logger.setVerbose(false);

        IDesignPatternRules patternsParser = new CommonPatternsParser();
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
