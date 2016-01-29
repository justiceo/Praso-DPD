package DPD.DSMMapper;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
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
}
