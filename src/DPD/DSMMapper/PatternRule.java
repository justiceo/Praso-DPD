package DPD.DSMMapper;

import DPD.Enums.RuleType;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Justice on 1/26/2016.
 */
@XmlRootElement(name = "rule")
public class PatternRule {
    @XmlAttribute(name = "rule-type")
    public RuleType ruleType;
    @XmlAttribute
    public String source;
    @XmlAttribute
    public String target;
    @XmlAttribute(required = false)
    public boolean exclude = false;

    @Override
    public boolean equals(Object other) {
        PatternRule otherRule = (PatternRule) other;
        return ruleType.equals(otherRule.ruleType)
                && source.equals(otherRule.source)
                && exclude == otherRule.exclude
                && target.equals(otherRule.target);
    }
}
