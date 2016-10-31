package DPD;

import DPD.Enums.ClassType;
import DPD.Enums.Flag;

import java.util.List;

/**
 * Created by Justice on 1/27/2016.
 */
public class JClass {
    /**
     * An int id for easy array access
     */
    public int classId;

    /**
     * Equivalent to Class name
     */
    public String typeId;

    /**
     * Equivalent to ClassName.java
     */
    public String fileName;
    public String filePath;
    public ClassType classType;
    public String[] matrixRow;
    public String dependencyLine;
    public List<Flag> flags;
    public List<Claim> claims;
}
