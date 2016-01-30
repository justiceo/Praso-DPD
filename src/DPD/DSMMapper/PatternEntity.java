package DPD.DSMMapper;

import DPD.Enums.ClassType;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * Created by Justice on 1/27/2016.
 */
@XmlRootElement(name="entity")
public class PatternEntity {
    @XmlAttribute
    public String id;
    @XmlAttribute
    public String name;
    @XmlAttribute
    public ClassType type;
    @XmlElement
    public List<String> compliantClasses;

    public boolean equals(Object other) {
        PatternEntity otherP = (PatternEntity) other;
        return id.equals(otherP.id)
                && name.equals(otherP.name)
                && type.equals(otherP.type)
                && (compliantClasses == null && otherP.compliantClasses == null || compliantClasses.equals(otherP.compliantClasses));
    }
}
