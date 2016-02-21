package DPD.SourceParser;

import DPD.DSMMapper.IPattern;
import DPD.Enums.ASTAnalysisType;
import DPD.Enums.DependencyType;

/**
 * Created by Justice on 2/15/2016.
 */
public interface ASTAnalyzer {
    String[] getAssociatedDependency(String sourceClassStr, DependencyType dependencyType);

    void validate(IPattern pattern);

    boolean examine(String niceName, ASTAnalysisType astAnalysisType, String niceName1);
}
