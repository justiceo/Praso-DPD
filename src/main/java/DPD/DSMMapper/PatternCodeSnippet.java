package DPD.DSMMapper;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

/**
 * Created by Justice on 3/27/2016.
 */
@XmlRootElement(name = "code-snippet")
public class PatternCodeSnippet implements Serializable {
    @XmlAttribute
    public String source;
    @XmlAttribute
    public String target;
    @XmlAttribute
    public String value;
    @XmlAttribute(required = false)
    public boolean exclude = false;

    @Override
    public boolean equals(Object other) {
        PatternRule otherRule = (PatternRule) other;
        return source.equals(otherRule.source)
                && exclude == otherRule.exclude
                && target.equals(otherRule.target)
                && value.equals(otherRule.value);
    }
}
