package DPD.DSMMapper;

import DPD.Enums.CardinalityType;
import DPD.Enums.DependencyType;
import DPD.DependencyBrowser.IBrowser;
import DPD.Enums.RuleType;

import org.apache.commons.lang3.SerializationUtils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Justice on 1/28/2016.
 */
public class RuleFilters {

    private boolean exclude = false; //todo: add this to rule
    private IBrowser browser;

    public RuleFilters(IBrowser browser) {
        this.browser = browser;
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
            for(PatternEntity pE: newPattern.entities) {
                if(pE.id.equals(entityToResolve.id)) {
                    pE.compliantClasses = new LinkedList<>();
                    pE.compliantClasses.add(className);
                }
            }
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
            return filter(pattern, rule.source, rule.target, DependencyType.valueOf(rule.value.toUpperCase()), rule.exclude);
        }
        else if(rule.ruleType.equals(RuleType.Cardinality)) {
            CardinalityType cardinality = CardinalityType.valueOf(rule.value.toUpperCase());
            if(cardinality.equals(CardinalityType.PLURAL))
                return filterIsPlural(pattern, rule.source);
            else if(cardinality.equals(CardinalityType.SINGULAR))
                return false; // filter is singular.
        }


        return false; // we haven't added this rule yet

    }

    public boolean filter(IPattern pattern, String sourceId, String targetId, DependencyType dependencyType, boolean exclude) {
        List<String> targetBucket = null;
        List<String> sourceBucket = null;
        int sourceBucketIndex = 0;
        int counter = 0;

        // get the entity buckets to filter
        for(PatternEntity entity: pattern.getEntities()) {
            if(entity.id.equals(targetId))
                targetBucket = entity.compliantClasses;
            else if(entity.id.equals(sourceId)) {
                sourceBucket = entity.compliantClasses;
                sourceBucketIndex = counter;
            }
            counter++;
        }

        // if any is empty, job is already done
        if(targetBucket.isEmpty() || sourceBucket.isEmpty())
            return false;

        // perform the filter
        List<String> filteredList = new ArrayList<>();
        for(String sourceClassStr: sourceBucket) {
            for(String classStr: browser.getAssociatedDependency(sourceClassStr, dependencyType)) {
                if(targetBucket.contains(classStr)) {
                    filteredList.add(sourceClassStr);
                    break;
                }
            }
        }

        if(exclude) {
            sourceBucket.removeAll(filteredList);
            filteredList = sourceBucket;
        }

        pattern.getEntities().get(sourceBucketIndex).compliantClasses = filteredList;
        return  !filteredList.isEmpty();
    }

    public boolean filterIsPlural(IPattern pattern, String subjectBucketId) {
        int bucketSize = 0;
        for(PatternEntity entity: pattern.getEntities()) {
            if(entity.id.equals(subjectBucketId))
                bucketSize = entity.compliantClasses.size();
        }
        return  bucketSize > 1;
    }
    public boolean filterIsAssociatedWithDependency(IPattern pattern, String subjectBucketId, String...depStr) {
        for(String str: depStr) {
            filterIsAssociatedWithDependency(pattern, subjectBucketId, DependencyType.valueOf(str));
        }
        PatternEntity bucket = pattern.getEntities().stream().filter(b -> b.id.equals(depStr)).findFirst().get();
        return !bucket.compliantClasses.isEmpty();
    }

    public boolean filterIsAssociatedWithDependency(IPattern pattern, String subjectBucketId, DependencyType dependencyType) {
        List<String> subjectBucket = null;
        int subjectBucketIndex = 0;
        List<String> filteredList = new ArrayList<>();
        int counter = 0;
        for(PatternEntity entity: pattern.getEntities()) {
            if(entity.id.equals(subjectBucketId)) {
                subjectBucket = entity.compliantClasses;
                subjectBucketIndex = counter;
            }
            counter++;
        }
        for(String className: subjectBucket) {
            if(browser.isAssociatedWithDependency(className, dependencyType)) {
                filteredList.add(className);
            }
        }

        pattern.getEntities().get(subjectBucketIndex).compliantClasses = filteredList;
        return  !filteredList.isEmpty();
    }
}
