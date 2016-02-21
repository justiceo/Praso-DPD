package DPD.DSMMapper;

import DPD.Enums.ASTAnalysisType;
import DPD.Enums.CardinalityType;
import DPD.Enums.DependencyType;
import DPD.DependencyBrowser.IBrowser;
import DPD.Enums.RuleType;

import DPD.SourceParser.ASTAnalyzer;
import DPD.SourceParser.JParser;
import DPD.SourceParser.ObserverASTAnalyzer;
import org.apache.commons.lang3.SerializationUtils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Justice on 1/28/2016.
 */
public class RuleFilters {

    private IBrowser browser;
    private ASTAnalyzer sourceParser;

    public RuleFilters(IBrowser browser) {
        this.browser = browser;
    } // todo: remove from constructor
    public void addSourceParser(ASTAnalyzer sourceParser) {
        this.sourceParser = sourceParser;
    }

    public List<IPattern> resolve(IPattern pattern, PatternResolver resolver) {
        // list to hold resolved patterns
        List<IPattern> resolvedPatterns = new LinkedList<>();
        // get entity to resolve
        PatternEntity entityToResolve = pattern.getEntities().stream().filter(e -> e.id.equals(resolver.source)).findFirst().get();

        // create new patterns from each item in the entity
        for(String className: entityToResolve.compliantClasses) {
            CommonPattern newPattern = SerializationUtils.deserialize(SerializationUtils.serialize(pattern));
            newPattern.name = pattern.getName() + " - " + browser.getNiceName(className);

            // reset it's entity to its self alone
            newPattern.entities.stream().filter(pE -> pE.id.equals(entityToResolve.id)).forEach(pE -> {
                pE.compliantClasses = new LinkedList<>();
                pE.compliantClasses.add(className);
            });
            resolvedPatterns.add(newPattern);
        }

        // apply the rules to each individual pattern
        for(IPattern pattern1: resolvedPatterns) {
            for(PatternRule rule: pattern1.getRules()) {
                filter(pattern1, rule);
            }
        }

        // remove patterns that are empty
        List<IPattern> incompletePatterns = new LinkedList<>();
        for(IPattern p: resolvedPatterns) {
            for(PatternEntity entity: p.getEntities()) {
                if(entity.compliantClasses.isEmpty()) {
                    incompletePatterns.add(p);
                }
            }
        }
        resolvedPatterns.removeAll(incompletePatterns);
        return resolvedPatterns;
    }

    public boolean filter(IPattern pattern, PatternRule rule) {
        if(rule.ruleType.equals(RuleType.Dependency)) {
            return dependencyFilter(pattern, rule.source, rule.target, DependencyType.valueOf(rule.value.toUpperCase()), rule.exclude);
        }
        else if(rule.ruleType.equals(RuleType.Cardinality)) {
            CardinalityType cardinality = CardinalityType.valueOf(rule.value.toUpperCase());
            if(cardinality.equals(CardinalityType.PLURAL))
                return cardinalityFilter(pattern, rule.source);
            else if(cardinality.equals(CardinalityType.SINGULAR))
                return false; // dependencyFilter is singular.
        }

        return false; // we haven't added this rule yet
    }

    public boolean dependencyFilter(IPattern pattern, String sourceId, String targetId, DependencyType dependencyType, boolean exclude) {
        // if any is empty, job is already done
        if(patternHasEmptyEntity(pattern))
            return false;

        List<String> targetBucket = null;
        List<String> sourceBucket = null;
        int sourceBucketIndex = 0;
        int counter = 0;

        // get the entity buckets to dependencyFilter
        for(PatternEntity entity: pattern.getEntities()) {
            if(entity.id.equals(targetId))
                targetBucket = entity.compliantClasses;
            else if(entity.id.equals(sourceId)) {
                sourceBucket = entity.compliantClasses;
                sourceBucketIndex = counter;
            }
            counter++;
        }

        // perform the dependencyFilter
        List<String> filteredList = new ArrayList<>();
        for(String sourceClassStr: sourceBucket) {
            for(String classStr: browser.getAssociatedDependency(sourceClassStr, dependencyType)) {
                if(targetBucket.contains(classStr)) {
                    filteredList.add(sourceClassStr);
                    break;
                }
            }
            // browser.does(sourceClass).have(dependencyType).on(targetClass).
        }

        if(exclude) {
            sourceBucket.removeAll(filteredList);
            filteredList = sourceBucket;
        }

        pattern.getEntities().get(sourceBucketIndex).compliantClasses = filteredList;
        return  !filteredList.isEmpty();
    }

    public boolean cardinalityFilter(IPattern pattern, String subjectBucketId) {
        int bucketSize = 0;
        for(PatternEntity entity: pattern.getEntities()) {
            if(entity.id.equals(subjectBucketId))
                bucketSize = entity.compliantClasses.size();
        }
        return  bucketSize > 1;
    }

    /* assumes work is only on a unit of the pattern */
    public boolean astAnalyzeFilter(IPattern pattern, String sourceId, String targetId, ASTAnalysisType astAnalysisType, boolean exclude) {
        if(patternHasEmptyEntity(pattern))
            return false;

        /*if(pattern.getName().equals("Observer Pattern")) { // un-hard code this.
            ObserverASTAnalyzer obs = new ObserverASTAnalyzer();
            obs.validate(pattern);

        }*/

        List<String> sourceBucket = pattern.getEntityById(sourceId).compliantClasses;
        //String sourceClass = pattern.getEntityById(sourceId).compliantClasses.get(0);
        String targetClass = pattern.getEntityById(targetId).compliantClasses.get(0);
        sourceParser = new JParser();
        for(String sourceClass: sourceBucket) {
            if (!sourceParser.examine(sourceClass, astAnalysisType, targetClass)) {
                PatternEntity bucket = pattern.getEntityById(sourceId);
                bucket.compliantClasses.remove(sourceClass);
                return false;
            }
        }

        return true;
    }

    private boolean patternHasEmptyEntity(IPattern pattern) {
        for(PatternEntity entity: pattern.getEntities())
            if(entity.compliantClasses.size() == 0) {
                return true;
            }
        return false;
    }

    public void checkSource(IPattern pattern, PatternRule rule) {
        if(rule.ruleType.equals(RuleType.AST_Analyze)) {
            astAnalyzeFilter(pattern, rule.source, rule.target, ASTAnalysisType.valueOf(rule.value), rule.exclude);
        }
    }
}
