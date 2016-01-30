package DPD.DSMMapper;

import DPD.DependencyBrowser.ClassType;
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

        for(PatternEntity pE: pattern.getEntities()) {
            pE.compliantClasses = new ArrayList<>();
            if(pE.type.equals(ClassType.Abstraction)) {
                pE.compliantClasses.addAll(browser.getClassesOfType(ClassType.Interface));
                pE.compliantClasses.addAll(browser.getClassesOfType(ClassType.Abstract));
            }
            else if(pE.type.equals(ClassType.Concrete)) {
                pE.compliantClasses.addAll(browser.getClassesOfType(ClassType.Class));
                pE.compliantClasses.addAll(browser.getClassesOfType(ClassType.Final));
                // add partial later
            }
            else {
                pE.compliantClasses = browser.getClassesOfType(pE.type);
            }
        }
    }

    @Override
    public List<IPattern> resolvePatternEntities(IPattern pattern) {
        return null;
    }
}
