package DPD.DSMMapper;

import DPD.DependencyBrowser.IBrowser;
import DPD.ILogger;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Justice on 1/28/2016.
 */
@XmlRootElement(name="pattern")
public class CommonPattern implements IPattern {

    @XmlAttribute String name;
    @XmlElement(name = "entity")
    List<PatternEntity> entities;
    @XmlElement (name = "rule")
    List<PatternRule> rules;
    @XmlElement (name = "resolver")
    List<PatternResolver> resolvers;


    @Override
    public String getName() {
        return name;
    }

    @Override
    public List<PatternEntity> getEntities() {
        return entities;
    }

    @Override
    public List<PatternRule> getRules() {
        return rules;
    }

    @Override
    public List<PatternResolver> getResolvers() {
        return resolvers;
    }

    @Override
    public void displayMembers(ILogger logger, IBrowser browser) {
        logger.log("\n======= begin display pattern ==========\n");
        logger.log("Pattern: " + name );

        for(PatternEntity entity: entities) {
            logger.log("\n" + entity.name + " is satisfied by: ");
            List<Integer> sorted = new ArrayList<>(entity.compliantClasses);
            Collections.sort(sorted);
            for(int classId: sorted) {
                logger.log("\t" + browser.getClassPath(classId));
            }
        }

        //logger.log("\n======= end display pattern ==========\n");

    }

    @Override
    public PatternEntity getEntityById(String id) {
        return entities.stream().filter(e -> e.id.equals(id)).findFirst().get();
    }

    @Override
    public boolean isVoid() {
        for(PatternEntity entity: entities)
            if(entity.compliantClasses.size() == 0) {
                return true;
            }
        return false;
    }
}
