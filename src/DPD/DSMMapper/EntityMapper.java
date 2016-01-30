package DPD.DSMMapper;

import DPD.DependencyBrowser.IBrowser;

import java.util.List;

/**
 * Created by Justice on 1/27/2016.
 *
 * This interfaces exposes methods for placing classes into entity buckets
 * When it is done, it should return a list of entity buckets
 * Each bucket contains classes that satisfy some base conditions
 */
public interface EntityMapper {

    List<PatternEntity> getEntityBuckets(List<String> classes);

    void init(IBrowser browser);

    void mapPatternEntities(IPattern pattern);

    List<IPattern> resolvePatternEntities(IPattern pattern);
}
