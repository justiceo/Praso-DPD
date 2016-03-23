package DPD.PatternParser;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * Created by Justice on 1/27/2016.
 */
@XmlRootElement
public class PatternConfig {
    @XmlAttribute
    public String id;
    @XmlAttribute
    public String name;
    @XmlAttribute(name = "config-file")
    public String configFile;
    @XmlAttribute
    public boolean include;

    public static boolean compareList(List<PatternConfig> listA, List<PatternConfig> listB) {
        for (PatternConfig config : listA) {
            if (!listB.contains(config))
                return false;
        }
        return true;
    }

    @Override
    public boolean equals(Object other) {
        PatternConfig otherConfig = (PatternConfig) other;
        return this.id.equals(otherConfig.id)
                && this.name.equals(otherConfig.name)
                && this.configFile.equals(otherConfig.configFile)
                && this.include == otherConfig.include;
    }
}