package DPD;

import DPD.Browser.EasyDSMQuery;
import DPD.Model.DSMFileModel;
import DPD.Model.DependencyType;
import DPD.REPL.ConsoleREPL;

import java.util.List;

/**
 * Created by Justice on 2/2/2016.
 */
public class Main {

    private static final String testDsmFile = "files\\dsm\\Maze.dsm";

    public static void main(String[] args) throws InterruptedException {
        DSMFileModel dsm = new DSMFileModel(testDsmFile);
        List<DependencyType> dependencyTypeList = Util.getDependencyTypes(dsm.exhibitedDependencyLine);
        EasyDSMQuery dsmQuery = new EasyDSMQuery(dsm.matrixLines, dsm.filePaths, dependencyTypeList);
        ConsoleREPL repl = new ConsoleREPL(dsmQuery);
        repl.start();
    }
}
