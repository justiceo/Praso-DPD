package DPD.SourceParser;

import DPD.Enums.ASTAnalysisType;

/**
 * Created by Justice on 2/15/2016.
 */
public interface ASTAnalyzer {
    boolean examine(String niceName, ASTAnalysisType astAnalysisType, String niceName1);
}
