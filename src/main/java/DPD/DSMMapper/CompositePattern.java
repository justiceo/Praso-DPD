package DPD.DSMMapper;

import DPD.DependencyBrowser.IBrowser;
import DPD.ILogger;

import java.util.List;

/**
 * Created by Justice on 3/23/2016.
 */
public class CompositePattern implements PatternComponent {

    public String getName() {
        return null;
    }

    public List<PatternEntity> getEntities() {
        return null;
    }

    public List<PatternRule> getRules() {
        return null;
    }

    public List<PatternResolver> getResolvers() {
        return null;
    }

    public void displayMembers(ILogger logger, IBrowser browser) {

    }

    public PatternEntity getEntityById(String id) {
        return null;
    }

    public boolean isVoid() {
        return false;
    }
}
