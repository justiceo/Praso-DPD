package DPD.DSMMapper;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * Created by Justice on 1/27/2016.
 */
@XmlRootElement(name="Pattern")
public interface IPattern {

    @XmlElement(name="name")
    String getName();

    @XmlElement(name="entities")
    List<PatternEntity> getEntities();

    @XmlElement(name="rules")
    List<PatternRule> getRules();
}
