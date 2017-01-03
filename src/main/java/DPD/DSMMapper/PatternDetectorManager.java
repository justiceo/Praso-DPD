package DPD.DSMMapper;

import DPD.DependencyBrowser.DSMBrowser;
import DPD.SourceParser.JParser;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Justice on 3/25/2016.
 */
public class PatternDetectorManager {
    private List<PatternComponent> patterns;
    private DSMBrowser browser;
    private List<Thread> patternThreads;
    private List<PatternDetector> detectors;
    private JParser sourceParser;

    public PatternDetectorManager(List<PatternComponent> patternComponentList, DSMBrowser browser, JParser sourceParser) {
        this.patterns = patternComponentList;
        this.browser = browser;
        this.sourceParser = sourceParser;
        patternThreads = new LinkedList<>();
        detectors = new LinkedList<>();
    }

    public void startPDs() {
        for (PatternComponent pattern : patterns) {
            PatternDetector patternDetector = new PatternDetector(browser, pattern);
            detectors.add(patternDetector);
            patternDetector.addSourceParser(sourceParser);
            Thread detectorT = new Thread(patternDetector);
            detectorT.setName(pattern.getName() + " thread");
            detectorT.start();
            patternThreads.add(detectorT);
        }

    }

    public List<PatternComponent> getResults() throws InterruptedException {
        for(Thread t: patternThreads) {
            t.join();
        }

        List<PatternComponent> results = new LinkedList<>();
        for(PatternDetector detector: detectors) {
            results.addAll(detector.resolvedPatterns);
        }
        return results;
    }
}
