package DPD.DependencyBrowser;

import DPD.Enums.ClassType;

import java.util.List;

/**
 * Created by Justice on 1/27/2016.
 */
public class JClass {
    public ClassType classType;
    public String[] matrixRow;
    public String dependencyLine;
    public int classId;
    public String classPath;
    public List<Flag> flags;
}
