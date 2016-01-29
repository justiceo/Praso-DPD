package DPD.DSMMapper;

import DPD.DSMBrowser.IBrowser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Justice on 1/27/2016.
 */
public class DSMMapper implements EntityMapper {
    private IBrowser browser;
    @Override
    public List<PatternEntity> getEntityBuckets(List<String> classes) {
        return null;
    }

    @Override
    public void init(IBrowser browser) {
        this.browser = browser;
    }

    @Override
    public void mapPatternEntities(IPattern pattern) {
        for(PatternEntity pE: pattern.getEntities()) {
            List<String> compliantClasses = browser.getClassesOfType(pE.type);
            pE.compliantClasses = new ArrayList<>();
            pE.compliantClasses.addAll(compliantClasses);
        }
    }

    @Override
    public List<IPattern> resolvePatternEntities(IPattern pattern) {
        return null;
    }
}
