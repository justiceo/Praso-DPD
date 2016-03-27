package DPD.DSMMapper;

import DPD.DependencyBrowser.IBrowser;
import DPD.Enums.ASTAnalysisType;
import DPD.Enums.DependencyType;
import DPD.SourceParser.ASTAnalyzer;
import org.apache.commons.lang3.SerializationUtils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Justice on 1/28/2016.
 */
public class PatternDetector implements Runnable {

    protected List<PatternComponent> resolvedPatterns = new ArrayList<>();
    private IBrowser browser;
    private ASTAnalyzer sourceParser;
    private PatternComponent patternC;

    public PatternDetector(IBrowser browser, PatternComponent pattern) {
        this.browser = browser;
        this.patternC = pattern;
    }

    public void mapPatternEntities() {
        for (PatternEntity pEntity : patternC.getEntities()) {
                pEntity.compliantClasses = browser.getClassesOfType(pEntity.type, pEntity.hasDependency, pEntity.value);
        }
    }

    public void addSourceParser(ASTAnalyzer sourceParser) {
        this.sourceParser = sourceParser;
    }

    public List<PatternComponent> resolve(PatternComponent pattern, PatternResolver resolver) {
        // list to hold resolved patterns
        List<PatternComponent> resolvedPatterns = new LinkedList<>();
        // get entity to resolve
        PatternEntity entityToResolve = pattern.getEntities().stream().filter(e -> e.id.equals(resolver.source)).findFirst().get();

        // create new patterns from each item in the entity
        for (String classId : entityToResolve.compliantClasses) {
            SimplePattern newPattern = SerializationUtils.deserialize(SerializationUtils.serialize(pattern));
            newPattern.name = pattern.getName() + " - " + browser.getType(classId);

            // reset it's entity to its self alone
            newPattern.entities.stream().filter(pE -> pE.id.equals(entityToResolve.id)).forEach(pE -> {
                pE.compliantClasses = new LinkedList<>();
                pE.compliantClasses.add(classId);
            });
            resolvedPatterns.add(newPattern);
        }

        // apply the rules to each individual pattern
        for (PatternComponent pattern1 : resolvedPatterns) {
            for (PatternRule rule : pattern1.getRules()) {
                filter(pattern1, rule);
            }
        }

        // remove patterns that are empty
        List<PatternComponent> incompletePatterns = new LinkedList<>();
        for (PatternComponent p : resolvedPatterns) {
            for (PatternEntity entity : p.getEntities()) {
                if (entity.compliantClasses.isEmpty()) {
                    incompletePatterns.add(p);
                }
            }
        }
        resolvedPatterns.removeAll(incompletePatterns);
        return resolvedPatterns;
    }

    public boolean filter(PatternComponent pattern, PatternRule rule) {
            return dependencyFilter(pattern, rule.source, rule.target, DependencyType.valueOf(rule.value.toUpperCase()), rule.exclude);
    }

    public boolean dependencyFilter(PatternComponent pattern, String sourceEntityId, String targetEntityId, DependencyType dependencyType, boolean exclude) {
        // if any is empty, job is already done
        if (pattern.isVoid())
            return false;

        // get the entity buckets to dependencyFilter
        List<String> targetBucket = pattern.getEntityById(targetEntityId).compliantClasses; // IObserver
        List<String> sourceBucket = pattern.getEntityById(sourceEntityId).compliantClasses; // Concrete observer

        // perform the dependencyFilter
        // for each concrete observer, check if it has implements/extends dependency on entity in e1
        List<String> filteredList = new ArrayList<>();
        for (String srcClass : sourceBucket) {
            List<String> domDependencies = browser.getDomDependencies(srcClass, dependencyType);
            for (String dClass : domDependencies) {
                if (targetBucket.contains(dClass)) {
                    filteredList.add(srcClass);
                    break;
                }
            }
            // browser.does(sourceClass).have(dependencyType).on(targetClass).
        }

        if (exclude) {
            sourceBucket.removeAll(filteredList);
            filteredList = sourceBucket;
        }

        pattern.getEntityById(sourceEntityId).compliantClasses = filteredList;
        return !filteredList.isEmpty();
    }

    public boolean cardinalityFilter(PatternComponent pattern, String subjectBucketId) {
        int bucketSize = 0;
        for (PatternEntity entity : pattern.getEntities()) {
            if (entity.id.equals(subjectBucketId))
                bucketSize = entity.compliantClasses.size();
        }
        return bucketSize > 1;
    }

    /* assumes work is only on a unit of the pattern
    public boolean astAnalyzeFilter(PatternComponent pattern, String sourceId, String targetId, ASTAnalysisType astAnalysisType, boolean exclude) {
        if (pattern.isVoid())
            return false;

        int targetClassId = pattern.getEntityById(targetId).compliantClasses.get(0);
        sourceParser = new JParser(new ConsoleLogger());

        Iterator<Integer> sourceClassIterator = pattern.getEntityById(sourceId).compliantClasses.iterator();
        while (sourceClassIterator.hasNext()) {
            int sourceClassId = sourceClassIterator.next();
            String claim = sourceParser.examine(browser.getClassPath(sourceClassId), astAnalysisType, browser.getType(targetClassId));
            if (claim != null) {
                browser.addClaim(sourceClassId, "ForLoop", claim );
            }
        }

        return true;
    }
    */

    public List<String> astAnalyzeFilter(List<String> sourceBucket, String targetType, ASTAnalysisType astAnalysisType) {
        List<String> positive = new LinkedList<>();
        for(String sourceClassId: sourceBucket) {
            String claim = sourceParser.examine(browser.getClassPath(sourceClassId), astAnalysisType, browser.getType(targetType));
            if (claim != null) {
                positive.add(sourceClassId);
                browser.addClaim(sourceClassId, "ForLoop", claim);
            }
        }
        return positive.isEmpty()? sourceBucket : positive;
    }

    public void checkSource(PatternComponent pattern, PatternCodeSnippet rule) {
            PatternEntity sourceEntity = pattern.getEntityById(rule.source);

            // target should be a hedge entity, and should contain one class after separation
            assert pattern.getEntityById(rule.target).compliantClasses.size() == 1;
            String firstClassInTarget =  pattern.getEntityById(rule.target).compliantClasses.get(0);
            List<String> pos = astAnalyzeFilter(sourceEntity.compliantClasses, firstClassInTarget, ASTAnalysisType.valueOf(rule.value));
            sourceEntity.compliantClasses = pos;
    }

    @Override
    public void run() {
        // map entities
        mapPatternEntities();

        // apply filters
        for (PatternRule rule : patternC.getRules()) {
            filter(patternC, rule);
        }

        // resolve patterns
        for (PatternResolver resolver : patternC.getResolvers()) {
            resolvedPatterns.addAll(resolve(patternC, resolver));
        }
        System.out.println("\ntotal patterns added: " + resolvedPatterns.size());

        // run ast
        for(PatternComponent pattern: resolvedPatterns) {
            if(pattern.getCodeSnippets() != null)
                for (PatternCodeSnippet snippet : pattern.getCodeSnippets()) {
                    checkSource(pattern, snippet);
                }
        }

        /* remove empty pattern
        Iterator<PatternComponent> pIterator = resolvedPatterns.iterator();
        while (pIterator.hasNext()) {
            PatternComponent pattern = pIterator.next();
            if (pattern.isVoid()) {
                pIterator.remove();
            }
        }*/
    }

    public void addResolvedPatternList(List<PatternComponent> resolvedPatterns) {
        this.resolvedPatterns = resolvedPatterns;
    }
}
