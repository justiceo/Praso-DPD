package DPD.PatternParser;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * Created by Justice on 1/27/2016.
 */
@XmlRootElement
public class Config {
    @XmlElement(name="pattern")
    public List<PatternConfig> patterns; // initialized from xml
}
