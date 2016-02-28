package DPD.DSMMapper;

import DPD.DependencyBrowser.IBrowser;
import DPD.ILogger;
import sun.nio.cs.ext.IBM037;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.List;

/**
 * Created by Justice on 1/27/2016.
 */
@XmlRootElement(name="Pattern")
public interface IPattern extends Serializable {

    @XmlElement(name="name")
    String getName();

    @XmlElement(name="entities")
    List<PatternEntity> getEntities();

    @XmlElement(name="rules")
    List<PatternRule> getRules();

    @XmlElement(name="resolver")
    List<PatternResolver> getResolvers();

    public void displayMembers(ILogger logger, IBrowser browser);

    public PatternEntity getEntityById(String id);
}
