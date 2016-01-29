package DPD.DSMMapper;

import DPD.DSMBrowser.DependencyType;
import DPD.DSMBrowser.IBrowser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Justice on 1/28/2016.
 */
public class RuleFilters {

    private boolean exclude = false;
    private IBrowser browser;

    public RuleFilters(IBrowser browser) {
        this.browser = browser;
    }

    public void filter(IPattern observerPattern) {

    }

    public boolean filterImplements(IPattern pattern, String concreteBucketId, String interfaceBucketId) {
        return  genericFilter(pattern, concreteBucketId, interfaceBucketId, DependencyType.IMPLEMENT);
    }

    public boolean filterUses(IPattern pattern, String userId, String subjectId) {
        return  genericFilter(pattern, userId, subjectId, DependencyType.USE);
    }

    public boolean filterTypes(IPattern pattern, String objectBucketId, String subjectBucketId) {
        return  genericFilter(pattern, objectBucketId, subjectBucketId, DependencyType.TYPED);
    }

    public boolean filterIsPlural(IPattern pattern, String subjectBucketId) {
        int bucketSize = 0;
        for(PatternEntity entity: pattern.getEntities()) {
            if(entity.id.equals(subjectBucketId))
                bucketSize = entity.compliantClasses.size();
        }
        return  bucketSize > 1;
    }

    private boolean genericFilter(IPattern observerPattern, String objectId, String subjectId, DependencyType dependencyType) {
        List<String> subjectBucket = null;
        List<String> objectBucket = null;
        int objectBucketIndex = 0;
        int counter = 0;
        for(PatternEntity entity: observerPattern.getEntities()) {
            if(entity.id.equals(subjectId))
                subjectBucket = entity.compliantClasses;
            else if(entity.id.equals(objectId)) {
                objectBucket = entity.compliantClasses;
                objectBucketIndex = counter;
            }
            counter++;
        }
        if(subjectBucket.isEmpty() || objectBucket.isEmpty())
            return false;

        List<String> filteredList = new ArrayList<>();
        for(String concreteClass: objectBucket) {
            for(String classStr: browser.getAssociatedDependency(concreteClass, dependencyType)) {
                if(subjectBucket.contains(classStr)) {
                    filteredList.add(concreteClass);
                    continue;
                }
            }
        }

        observerPattern.getEntities().get(objectBucketIndex).compliantClasses = filteredList;
        return  !filteredList.isEmpty();
    }
}
