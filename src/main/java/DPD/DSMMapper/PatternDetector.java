package DPD.DSMMapper;

import DPD.ConsoleLogger;
import DPD.DependencyBrowser.IBrowser;
import DPD.Enums.ASTAnalysisType;
import DPD.Enums.CardinalityType;
import DPD.Enums.DependencyType;
import DPD.Enums.RuleType;
import DPD.SourceParser.ASTAnalyzer;
import DPD.SourceParser.JParser;
import org.apache.commons.lang3.SerializationUtils;

import java.util.ArrayList;
import java.util.Iterator;
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
            pEntity.compliantClasses = browser.getClassesOfType(pEntity.type, pEntity.hasDependency);
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
        for (int classId : entityToResolve.compliantClasses) {
            SimplePattern newPattern = SerializationUtils.deserialize(SerializationUtils.serialize(pattern));
            newPattern.name = pattern.getName() + " - " + browser.getClassPath(classId);

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
        if (rule.ruleType.equals(RuleType.Dependency)) {
            return dependencyFilter(pattern, rule.source, rule.target, DependencyType.valueOf(rule.value.toUpperCase()), rule.exclude);
        } else if (rule.ruleType.equals(RuleType.Cardinality)) {
            CardinalityType cardinality = CardinalityType.valueOf(rule.value.toUpperCase());
            if (cardinality.equals(CardinalityType.PLURAL))
                return cardinalityFilter(pattern, rule.source);
            else if (cardinality.equals(CardinalityType.SINGULAR))
                return false; // dependencyFilter is singular.
        }

        return false; // we haven't added this rule yet
    }

    public boolean dependencyFilter(PatternComponent pattern, String sourceEntityId, String targetEntityId, DependencyType dependencyType, boolean exclude) {
        // if any is empty, job is already done
        if (patternHasEmptyEntity(pattern))
            return false;

        // get the entity buckets to dependencyFilter
        List<Integer> targetBucket = pattern.getEntityById(targetEntityId).compliantClasses; // IObserver
        List<Integer> sourceBucket = pattern.getEntityById(sourceEntityId).compliantClasses; // Concrete observer

        // perform the dependencyFilter
        // for each concrete observer, check if it has implements/extends dependency on entity in e1
        List<Integer> filteredList = new ArrayList<>();
        for (Integer srcClass : sourceBucket) {
            List<Integer> auxDependencies = browser.getDomDependencies(srcClass, dependencyType);
            for (Integer auxClass : auxDependencies) {
                if (targetBucket.contains(auxClass)) {
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

    /* assumes work is only on a unit of the pattern */
    public boolean astAnalyzeFilter(PatternComponent pattern, String sourceId, String targetId, ASTAnalysisType astAnalysisType, boolean exclude) {
        if (patternHasEmptyEntity(pattern))
            return false;

        int targetClassId = pattern.getEntityById(targetId).compliantClasses.get(0);
        sourceParser = new JParser(new ConsoleLogger());

        Iterator<Integer> sourceClassIterator = pattern.getEntityById(sourceId).compliantClasses.iterator();
        while (sourceClassIterator.hasNext()) {
            int sourceClassId = sourceClassIterator.next();
            if (!sourceParser.examine(browser.getClassPath(sourceClassId), astAnalysisType, browser.getType(targetClassId))) {
                sourceClassIterator.remove();
            }
        }

        return true;
    }

    public List<Integer> astAnalyzeFilter(List<Integer> sourceBucket, int targetClassId, ASTAnalysisType astAnalysisType, boolean exclude) {
        List<Integer> positive = new LinkedList<>();
        for(int sourceClassId: sourceBucket) {
            if (sourceParser.examine(browser.getClassPath(sourceClassId), astAnalysisType, browser.getType(targetClassId))) {
                positive.add(sourceClassId);
            }
        }

        // todo: add exclude

        return positive;
    }

    private boolean patternHasEmptyEntity(PatternComponent pattern) {
        return pattern.isVoid(); // todo: refactor
    }

    public void checkSource(PatternComponent pattern, PatternRule rule) {
        if (rule.ruleType.equals(RuleType.AST_Analyze)) {
            astAnalyzeFilter(pattern, rule.source, rule.target, ASTAnalysisType.valueOf(rule.value), rule.exclude);
            /*
            PatternEntity sourceEntity = pattern.getEntityById(rule.source);
            int firstClassInTarget =  pattern.getEntityById(rule.target).compliantClasses.get(0); // todo: apply to all classes in subject
            List<Integer> pos = astAnalyzeFilter(sourceEntity.compliantClasses, firstClassInTarget, ASTAnalysisType.valueOf(rule.value), rule.exclude);
            sourceEntity.compliantClasses = pos;
            */
        }
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
            for (PatternRule rule : pattern.getRules()) {
                checkSource(pattern, rule);
            }
        }

        // remove empty pattern
        Iterator<PatternComponent> pIterator = resolvedPatterns.iterator();
        while (pIterator.hasNext()) {
            PatternComponent pattern = pIterator.next();
            if (pattern.isVoid()) {
                pIterator.remove();
            }
        }
    }

    public void addResolvedPatternList(List<PatternComponent> resolvedPatterns) {
        this.resolvedPatterns = resolvedPatterns;
    }
}
