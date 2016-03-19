package DPD.DSMMapper;

import DPD.Enums.ResolverType;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.List;

/**
 * Created by Justice on 1/29/2016.
 */
@XmlRootElement(name = "resolver")
public class PatternResolver implements Serializable {
    @XmlAttribute
    public String source;
    @XmlAttribute(name="resolver-type")
    public ResolverType resolverType;
}