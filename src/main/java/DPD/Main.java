package DPD;

import DPD.Browser.EasyDSMQuery;
import DPD.Model.DSMModel;
import DPD.Model.DependencyType;
import DPD.REPL.ConsoleREPL;
import DPD.REPL.FileREPL;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

/**
 * Created by Justice on 2/2/2016.
 */
public class Main {

    private static final String testDsmFile = "files\\dsm\\java-design-patterns.dsm";

    public static void main(String[] args) throws InterruptedException, FileNotFoundException {
        //runAllFiles();
        //runParticularFile();

        runOnConsole();
    }

    public static void runOnConsole() throws FileNotFoundException {
        DSMModel dsm = DSMModel.parse(testDsmFile);
        List<DependencyType> dependencyTypeList = Util.getDependencyTypes(dsm.exhibitedDependencyLine);
        EasyDSMQuery dsmQuery = new EasyDSMQuery(dsm.matrixLines, dsm.filePaths, dependencyTypeList);
        ConsoleREPL repl = new ConsoleREPL(dsmQuery);
        repl.start();
    }

    public static void runAllFiles() throws InterruptedException, FileNotFoundException {
        File dir = new File("files\\rules\\");
        File[] entries = dir.listFiles();
        DSMModel dsm = DSMModel.parse(testDsmFile);
        List<DependencyType> dependencyTypeList = Util.getDependencyTypes(dsm.exhibitedDependencyLine);
        EasyDSMQuery dsmQuery = new EasyDSMQuery(dsm.matrixLines, dsm.filePaths, dependencyTypeList);
        for(File f: entries){
            if(!isPatternFile(f.getName())) continue;
            System.out.println("Currently executing: " + f.getName());
            FileREPL reader = new FileREPL(f.getAbsolutePath(), dsmQuery);
            reader.run();
            Thread.sleep(500);
        }

    }

    public static void runParticularFile() throws FileNotFoundException {
        File f = new File("files\\rules\\composite.dpd");
        DSMModel dsm = DSMModel.parse(testDsmFile);
        List<DependencyType> dependencyTypeList = Util.getDependencyTypes(dsm.exhibitedDependencyLine);
        EasyDSMQuery dsmQuery = new EasyDSMQuery(dsm.matrixLines, dsm.filePaths, dependencyTypeList);
        System.out.println("Currently executing: " + f.getName());
        FileREPL reader = new FileREPL(f.getAbsolutePath(), dsmQuery);
        reader.run();
    }

    public static boolean isPatternFile(String fileName) {
        if(!fileName.contains("."))
            return false;
        fileName = fileName.substring(fileName.lastIndexOf("."));
        return fileName.equals(".dpd");
    }
}
