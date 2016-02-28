package DPD.DSMMapper;

import DPD.Enums.ClassType;
import DPD.DependencyBrowser.IBrowser;
import DPD.Enums.DependencyType;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Justice on 1/27/2016.
 */
public class EntityMapper {

    private IBrowser browser;

    public EntityMapper(IBrowser browser) {
        this.browser = browser;
    }

    public void mapPatternEntities(IPattern pattern) {
        for(PatternEntity pEntity: pattern.getEntities()) {
            pEntity.compliantClasses = browser.getClassesOfType(pEntity.type, pEntity.hasDependency);
        }
    }
}
