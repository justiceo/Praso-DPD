package DPD.DSMMapper;

import DPD.Enums.RuleTypes;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Objects;

/**
 * Created by Justice on 1/26/2016.
 */
@XmlRootElement(name = "rule")
public class PatternRule {
    @XmlAttribute(name = "type")
    public RuleTypes ruleType;
    @XmlAttribute
    public String source;
    @XmlAttribute
    public String target;

    @Override
    public boolean equals(Object other) {
        PatternRule otherRule = (PatternRule) other;
        return ruleType.equals(otherRule.ruleType)
                && source.equals(otherRule.source)
                && target.equals(otherRule.target);
    }
}
