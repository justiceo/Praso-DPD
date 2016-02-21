package DPD.SourceParser;

import DPD.DSMMapper.IPattern;
import DPD.Enums.ASTAnalysisType;
import DPD.Enums.DependencyType;

/**
 * Created by Justice on 2/17/2016.
 */
public class ObserverASTAnalyzer implements  ASTAnalyzer{

    @Override
    public String[] getAssociatedDependency(String sourceClassStr, DependencyType dependencyType) {
        return new String[0];
    }

    @Override
    public void validate(IPattern pattern) {

    }

    @Override
    public boolean examine(String niceName, ASTAnalysisType astAnalysisType, String niceName1) {
        return false;
    }
}
