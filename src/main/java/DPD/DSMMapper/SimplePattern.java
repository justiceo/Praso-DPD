package DPD.DSMMapper;

import DPD.Claim;
import DPD.DependencyBrowser.DSMBrowser;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by Justice on 1/28/2016.
 */
@XmlRootElement(name = "pattern")
public class SimplePattern implements PatternComponent {

    @XmlAttribute
    String name;
    @XmlElement(name = "entity")
    List<PatternEntity> entities;
    @XmlElement(name = "rule")
    List<PatternRule> rules;
    @XmlElement(name = "resolver")
    List<PatternResolver> resolvers;
    @XmlElement(name = "code-snippet")
    List<PatternCodeSnippet> codeSnippets;


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
    public List<PatternCodeSnippet> getCodeSnippets() {
        return codeSnippets;
    }

    @Override
    public void displayMembers(Logger logger, DSMBrowser browser) {
        logger.info("\n======= begin display pattern ==========");
        logger.info("Pattern: " + name);

        for (PatternEntity entity : entities) {
            logger.info("\n" + entity.name + " is satisfied by: ");
            List<String> sorted = new ArrayList<>(entity.compliantClasses);
            //Collections.sort(sorted);
            for (String classId : sorted) {
                logger.info("\t" + classId + " (id:" + browser.getId(classId) + ")");
                List<Claim> claims = browser.getClaims(classId);
                if(claims != null)
                for(Claim c: claims)
                    System.out.println("*** " +c.key + ": " + c.value + " ***");
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
        for (PatternEntity entity : entities)
            if (entity.compliantClasses.size() == 0) {
                return true;
            }
        return false;
    }
}
