package DPD.DSMMapper;

import DPD.Enums.ResolverType;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * Created by Justice on 1/29/2016.
 */
@XmlRootElement(name = "resolver")
public class PatternResolver {
    public String entity;
    public ResolverType resolverType;
}