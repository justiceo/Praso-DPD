package DPD.DSMMapper;

import DPD.Enums.ClassType;
import DPD.DependencyBrowser.IBrowser;

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

        for(PatternEntity pEntity: pattern.getEntities()) {
            pEntity.compliantClasses = new ArrayList<>();
            if(pEntity.type.equals(ClassType.Abstraction)) {
                pEntity.compliantClasses.addAll(browser.getClassesOfType(ClassType.Interface, pEntity.hasDependency));
                pEntity.compliantClasses.addAll(browser.getClassesOfType(ClassType.Abstract, pEntity.hasDependency));
            }
            else {
                pEntity.compliantClasses = browser.getClassesOfType(pEntity.type, pEntity.hasDependency);
            }
        }
    }

    @Override
    public List<IPattern> resolvePatternEntities(IPattern pattern) {
        return null;
    }
}
