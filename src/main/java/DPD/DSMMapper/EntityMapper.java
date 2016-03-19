package DPD.DSMMapper;

import DPD.DependencyBrowser.IBrowser;
import DPD.ILogger;

/**
 * Created by Justice on 1/27/2016.
 */
public class EntityMapper {

    private IBrowser browser;

    public EntityMapper(IBrowser browser, ILogger logger) {
        this.browser = browser;
    }

    public void mapPatternEntities(IPattern pattern) {
        for(PatternEntity pEntity: pattern.getEntities()) {
            pEntity.compliantClasses = browser.getClassesOfType(pEntity.type, pEntity.hasDependency);
        }
    }
}
