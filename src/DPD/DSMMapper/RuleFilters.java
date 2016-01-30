package DPD.DSMMapper;

import DPD.Enums.DependencyType;
import DPD.DependencyBrowser.IBrowser;
import DPD.Enums.RuleType;

import java.util.ArrayList;
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

    public boolean filter(IPattern pattern, PatternRule rule) {
        if(rule.ruleType.equals(RuleType.Implements)) {
            return filter(pattern, rule.source, rule.target, DependencyType.IMPLEMENT, rule.exclude); // use formal exclude
        }
        else if(rule.ruleType.equals(RuleType.Uses)) {
            return filter(pattern, rule.source, rule.target, DependencyType.USE, rule.exclude);
        }
        else if(rule.ruleType.equals(RuleType.Types)) {
            return filter(pattern, rule.source, rule.target, DependencyType.TYPED, rule.exclude);
        }
        else if(rule.ruleType.equals(RuleType.Extends)) {
            return filter(pattern, rule.source, rule.target, DependencyType.EXTEND, rule.exclude);
        }
        else if(rule.ruleType.equals(RuleType.IsPlural)) {
            return filterIsPlural(pattern, rule.source); // instead of exclude, use filterSingular.
        }
        else
            return false; // we haven't added this rule yet

    }

    public boolean filter(IPattern pattern, String sourceId, String targetId, DependencyType dependencyType, boolean exclude) {
        List<String> targetBucket = null;
        List<String> sourceBucket = null;
        int sourceBucketIndex = 0;
        int counter = 0;
        for(PatternEntity entity: pattern.getEntities()) {
            if(entity.id.equals(targetId))
                targetBucket = entity.compliantClasses;
            else if(entity.id.equals(sourceId)) {
                sourceBucket = entity.compliantClasses;
                sourceBucketIndex = counter;
            }
            counter++;
        }
        if(targetBucket.isEmpty() || sourceBucket.isEmpty())
            return false;

        //todo: test for exclude
        List<String> filteredList = new ArrayList<>();
        for(String concreteClass: sourceBucket) {
            for(String classStr: browser.getAssociatedDependency(concreteClass, dependencyType)) {
                if(targetBucket.contains(classStr)) {
                    filteredList.add(concreteClass);
                    continue;
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

    public boolean filterImplements(IPattern pattern, String concreteBucketId, String interfaceBucketId) {
        return  filter(pattern, concreteBucketId, interfaceBucketId, DependencyType.IMPLEMENT, exclude);
    }

    public boolean filterUses(IPattern pattern, String userId, String subjectId) {
        return  filter(pattern, userId, subjectId, DependencyType.USE, exclude);
    }

    public boolean filterTypes(IPattern pattern, String objectBucketId, String subjectBucketId) {
        return  filter(pattern, objectBucketId, subjectBucketId, DependencyType.TYPED, exclude);
    }


    public boolean filterIsPlural(IPattern pattern, String subjectBucketId) {
        int bucketSize = 0;
        for(PatternEntity entity: pattern.getEntities()) {
            if(entity.id.equals(subjectBucketId))
                bucketSize = entity.compliantClasses.size();
        }
        return  bucketSize > 1;
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

    /*
    private boolean genericFilter(IPattern pattern, String sourceId, String targetId, DependencyType dependencyType) {
        List<String> targetBucket = null;
        List<String> sourceBucket = null;
        int sourceBucketIndex = 0;
        int counter = 0;
        for(PatternEntity entity: pattern.getEntities()) {
            if(entity.id.equals(targetId))
                targetBucket = entity.compliantClasses;
            else if(entity.id.equals(sourceId)) {
                sourceBucket = entity.compliantClasses;
                sourceBucketIndex = counter;
            }
            counter++;
        }
        if(targetBucket.isEmpty() || sourceBucket.isEmpty())
            return false;

        List<String> filteredList = new ArrayList<>();
        for(String concreteClass: sourceBucket) {
            for(String classStr: browser.getAssociatedDependency(concreteClass, dependencyType)) {
                if(targetBucket.contains(classStr)) {
                    filteredList.add(concreteClass);
                    continue;
                }
            }
        }

        pattern.getEntities().get(sourceBucketIndex).compliantClasses = filteredList;
        return  !filteredList.isEmpty();
    }*/
}
