package DPD;

import DPD.Enums.ClassType;
import DPD.Enums.Flag;

import java.util.List;

/**
 * Created by Justice on 1/27/2016.
 */
public class JClass {
    public int classId;
    public String typeId;
    public String fileName;
    public String filePath;
    public ClassType classType;
    public String[] matrixRow;
    public String dependencyLine;
    public List<Flag> flags;
    public List<Claim> claims;
}
