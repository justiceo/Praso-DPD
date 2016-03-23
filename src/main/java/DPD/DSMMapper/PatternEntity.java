package DPD.DSMMapper;

import DPD.Enums.ClassType;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.List;

/**
 * Created by Justice on 1/27/2016.
 */
@XmlRootElement(name = "entity")
public class PatternEntity implements Serializable {
    @XmlAttribute
    public String id;
    @XmlAttribute
    public String name;
    @XmlAttribute
    public ClassType type;
    @XmlAttribute
    public String hasDependency;
    @XmlElement
    public List<Integer> compliantClasses;

    public List<String> claims;

    public boolean equals(Object other) {
        PatternEntity otherP = (PatternEntity) other;
        return id.equals(otherP.id)
                && name.equals(otherP.name)
                && type.equals(otherP.type)
                && hasDependency.equals(otherP.hasDependency)
                && (compliantClasses == null && otherP.compliantClasses == null || compliantClasses.equals(otherP.compliantClasses));
    }
}
