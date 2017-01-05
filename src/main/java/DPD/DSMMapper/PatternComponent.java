package DPD.DSMMapper;

import DPD.DependencyBrowser.DSMBrowser;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by Justice on 1/27/2016.
 */
@XmlRootElement(name = "Pattern")
public interface PatternComponent extends Serializable {

    @XmlElement(name = "name")
    String getName();

    @XmlElement(name = "entities")
    List<PatternEntity> getEntities();

    @XmlElement(name = "rules")
    List<PatternRule> getRules();

    @XmlElement(name = "resolver")
    List<PatternResolver> getResolvers();

    @XmlElement(name = "code-snippet")
    List<PatternCodeSnippet> getCodeSnippets();

    void displayMembers(Logger logger, DSMBrowser browser);

    PatternEntity getEntityById(String id);

    boolean isVoid();

}
